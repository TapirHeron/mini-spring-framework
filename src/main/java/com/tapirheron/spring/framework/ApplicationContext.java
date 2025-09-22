package com.tapirheron.spring.framework;

import com.tapirheron.spring.dao.Mapper;
import com.tapirheron.spring.dao.MySqlSessionFactory;
import com.tapirheron.spring.framework.properties.Configuration;
import com.tapirheron.spring.framework.properties.ConfigurationProperties;
import com.tapirheron.spring.framework.properties.EnableConfigurationProperties;
import com.tapirheron.spring.framework.properties.Value;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 应用上下文类，负责管理Spring容器中的Bean
 * <p>
 * ApplicationContext是Spring框架的核心，负责Bean的创建、配置和管理。
 * 它提供了依赖注入、自动装配、生命周期管理等功能。
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Data
@Slf4j
public class ApplicationContext {

    /**
     * 存储已完全初始化的Bean实例
     */
    private final Map<String, Object> ioc;

    /**
     * 存储正在创建中的Bean实例，用于解决循环依赖问题
     */
    private final Map<String, Object> loadingIoc;

    /**
     * 存储Bean定义信息
     */
    private final Map<String, BeanDefinition> beanDefinitionMap;

    /**
     * 存储Bean后置处理器
     */
    private final List<BeanPostProcessor> beanPostProcessors;

    /**
     * 存储配置文件中的键值对
     */
    private final Map<String, String> configValueMap;
    /**
     * 包名，用于扫描该包下的组件
     */
    private final String packageName;


    /**
     * 构造函数，初始化应用上下文
     *
     * @param packageName 包名，用于扫描该包下的组件
     * @param args        启动参数
     */
    @SuppressWarnings("all")
    public ApplicationContext(String packageName, String[] args) {
        ioc = new HashMap<>();
        loadingIoc = new HashMap<>();
        beanDefinitionMap = new HashMap<>();
        beanPostProcessors = new ArrayList<>();
        configValueMap = new HashMap<>();
        this.packageName = packageName;
        /* 添加ApplicationContext实例到loadingIOC容器中,
        不放在ioc中是因为优先读取laodingIoc,
        以防在ioc还没初始化完成之前，
        有bean依赖applicationContext(如dispatcherServlet)
         */
        loadingIoc.put("applicationContext", this);
        initContext("com.tapirheron.spring");
        initMapper(packageName);
        initMapper("com.tapirheron.spring");
        // 加载完所有mapper后再初始化包下面的类
        initContext(packageName);

    }


    private void initMapper(String packageName) {
        scanPackage(packageName)
                .forEach(this::createMapper);
    }


    /**
     * 初始化Bean后置处理器
     */
    private void initBeanPostProcessors() {
        beanDefinitionMap.values()
                .stream()
                .filter(beanDefinition -> BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanType()))
                .map(beanDefinition -> {
                    try {
                        BeanPostProcessor bean = (BeanPostProcessor)beanDefinition.getConstructor().newInstance();
                        loadingIoc.put(beanDefinition.getBeanName(), bean);
                        return bean;
                    } catch (Exception ignore) {}
                    return null;
                })
                .forEach(beanPostProcessors::add);
    }

    /**
     * 初始化上下文
     *
     * @param packageName 包名
     */
    private void initContext(String packageName) {
        loadConfigValueMap();
        scanPackage(packageName).stream()
                .peek(this::initConfigurationProperties)
                .peek(this::initConfigurationBean)
                .filter(this::canCreateBean)
                .forEach(this::wrapperBean);
        initBeanPostProcessors();
        beanDefinitionMap.values()
                .stream()
                .sorted((beanDefinition1, beanDefinition2) -> {
                    // 按照优先级加载
                    Class<?> beanType1 = beanDefinition1.getBeanType();
                    Class<?> beanType2 = beanDefinition2.getBeanType();
                    if (beanType1.isAnnotationPresent(Order.class) && beanType2.isAnnotationPresent(Order.class)) {
                        return beanType2.getAnnotation(Order.class).value() - beanType1.getAnnotation(Order.class).value();
                    }
                    if (beanType1.isAnnotationPresent(Order.class) && !beanType2.isAnnotationPresent(Order.class)) {
                        return -1;
                    }
                    if (!beanType1.isAnnotationPresent(Order.class) && beanType2.isAnnotationPresent(Order.class)) {
                        return 1;
                    }
                    return 0;
                })
                .forEach(this::createBean);

    }

    private <T> void initConfigurationProperties(Class<T> aClass) {
        if (!aClass.isAnnotationPresent(ConfigurationProperties.class)) {
            return;
        }
        T o = null;
        try {
            o = aClass.getConstructor().newInstance();
        } catch (Exception ignore) {}
        ConfigurationProperties annotation = aClass.getAnnotation(ConfigurationProperties.class);
        String prefix = annotation.prefix();
        T finalO = o;

        Arrays.stream(aClass.getDeclaredFields())
                .peek(field -> {
                    field.setAccessible(true);
                    String key = prefix.concat(".")
                            .concat(doFieldNameToConfigPropertiesName(field.getName()));
                    String value = configValueMap.get(key);
                    if (value == null) {
                        return;
                    }
                    if (field.getType().equals(int.class)) {
                        try {
                            field.set(finalO, Integer.parseInt(value));
                            return;
                        } catch (IllegalAccessException ignore) {}
                    }
                    if (field.getType().equals(float.class)) {
                        try {
                            field.set(finalO, Float.parseFloat(value));
                            return;
                        } catch (IllegalAccessException ignore) {}
                    }
                    try {
                        field.set(finalO, value);
                    } catch (IllegalAccessException ignore) {}
                })
                // 触发stream懒加载执行
                .forEach(field -> {});
        ioc.put(String.valueOf(Character.toLowerCase(aClass.getSimpleName().charAt(0)))
                .concat(aClass.getSimpleName().substring(1)), finalO);
    }

    /**
     * 将字段名转换为配置属性名
     *
     * @param name 字段名
     * @return 配置属性名
     */
    private String doFieldNameToConfigPropertiesName(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                stringBuilder.append("-").append(Character.toLowerCase(c));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    private <T> void createMapper(Class<T> aClass) {
        if (aClass.isAnnotationPresent(Mapper.class)) {
            Mapper mapperAnnotation = aClass.getAnnotation(Mapper.class);
            String mapperName = mapperAnnotation.value().isEmpty() ?
                    Character.toLowerCase(aClass.getSimpleName().charAt(0)) + aClass.getSimpleName().substring(1) :
                    mapperAnnotation.value();
            T mapper = getBean(MySqlSessionFactory.class).getMapper(aClass);
            this.ioc.put(mapperName, mapper);
        }
    }

    /**
     * 初始化配置类中的Bean
     *
     * @param aClass 配置类
     */
    private void initConfigurationBean(Class<?> aClass) {
        if (!aClass.isAnnotationPresent(Configuration.class)) {
            return;
        }
        Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .forEach(method -> {
                    Bean beanAnnotation = method.getAnnotation(Bean.class);
                    String beanName = beanAnnotation.value().isEmpty() ? method.getName() : beanAnnotation.value();
                    Object configClassInstance;
                    try {
                        if (method.getParameterCount() <= 0) {
                            configClassInstance = aClass.getConstructor().newInstance();
                            method.setAccessible(true);
                            Object beanInstance = method.invoke(configClassInstance);
                            ioc.put(beanName, beanInstance);
                        } else {
                            Parameter[] parameters = method.getParameters();
                            Object[] args = new Object[parameters.length];
                            int index = 0;
                            // 传递参数
                            for (Parameter parameter : parameters) {
                                Object value;
                                if (!aClass.isAnnotationPresent(EnableConfigurationProperties.class) ||
                                        !aClass.isAnnotationPresent(Configuration.class)) {
                                    break;
                                }
                                Class<?>[] enableClasses = aClass.getAnnotation(EnableConfigurationProperties.class).value();
                                if (Arrays.asList(enableClasses).contains(parameter.getType()) && (value = getBean(parameter.getType())) != null) {
                                    args[index++] = value;
                                }
                            }
                            configClassInstance = aClass.getConstructor().newInstance();
                            method.setAccessible(true);
                            Object beanInstance = method.invoke(configClassInstance, args);
                            ioc.put(beanName, beanInstance);
                        }
                    } catch (Exception ignore) {}
                });
    }

    /**
     * 加载配置文件中的键值对
     */
    private void loadConfigValueMap() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("application.properties"))))) {
            reader.lines()
                    .forEach(line -> {
                        int i = line.indexOf("=");
                        String key = line.substring(0, i);
                        String value = line.substring(i + 1);
                        configValueMap.put(key, value);
                    });
        } catch (Exception ignore) {}
    }

    /**
     * 创建Bean实例
     *
     * @param beanDefinition Bean定义
     * @return Bean实例
     */
    private Object createBean(BeanDefinition beanDefinition) {
        if (ioc.containsKey(beanDefinition.getBeanName())) {
            return ioc.get(beanDefinition.getBeanName());
        }
        return doCreateBean(beanDefinition);
    }

    /**
     * 执行Bean创建的具体逻辑
     *
     * @param beanDefinition Bean定义
     * @return Bean实例
     */
    private Object doCreateBean(BeanDefinition beanDefinition) {
        Object bean;
        if ((bean = getBean(beanDefinition.getBeanType())) != null) {
            // 注入配置文件属性
            injectConfigValue(beanDefinition, bean);
            // 注入属性
            autowireBeans(beanDefinition, bean);

            // 执行postConstruct方法
            initializeBean(beanDefinition, bean);
            return bean;
        }
        // 创建bean
        if ((bean = loadingIoc.get(beanDefinition.getBeanName())) == null) {
            try {
                Constructor<?> constructor = beanDefinition.getConstructor();
                bean = constructor.newInstance();
            } catch (Exception ignore) {}
            loadingIoc.put(beanDefinition.getBeanName(), bean);
        }

        // 注入配置文件属性
        injectConfigValue(beanDefinition, bean);
        // 注入属性
        autowireBeans(beanDefinition, bean);

        // 执行postConstruct方法
        initializeBean(beanDefinition, bean);
        if (beanDefinition.getBeanType() == MySqlSessionFactory.class) {
            initMapper("com.tapirheron.spring");
            initMapper(packageName);
        }
        ioc.put(beanDefinition.getBeanName(), loadingIoc.remove(beanDefinition.getBeanName()));
        return bean;
    }


    /**
     * 注入配置文件中的值到Bean的字段中
     *
     * @param beanDefinition Bean定义
     * @param bean           Bean实例
     */
    private void injectConfigValue(BeanDefinition beanDefinition, Object bean) {
        for (Field field : beanDefinition.getConfigValueFields()) {
            if (!field.isAnnotationPresent(Value.class)) {
                continue;
            }
            String annotationValue = field.getAnnotation(Value.class).value();
            field.setAccessible(true);
            // 不是${xxx}的值，直接给字段赋值
            if (!(annotationValue.startsWith("${") && annotationValue.endsWith("}"))) {
                try {
                    field.set(bean, annotationValue);
                } catch (IllegalAccessException ignore) {}
            }
            String key = annotationValue.substring(2, annotationValue.length() - 1);
            String value = configValueMap.get(key.isBlank() ? field.getName() : key);
            if (value == null) {
                continue;
            }
            if (field.getType().equals(int.class)) {
                try {
                    field.set(bean, Integer.parseInt(value));
                } catch (IllegalAccessException ignore) {
                }
            } else if (field.getType().equals(float.class)) {
                try {
                    field.set(bean, Float.parseFloat(value));
                } catch (IllegalAccessException ignore) {
                }
            } else {
                try {
                    field.set(bean, value);
                } catch (IllegalAccessException ignore) {}
            }
        }
    }

    /**
     * 初始化Bean，执行初始化方法
     *
     * @param beanDefinition Bean定义
     * @param bean           Bean实例
     */
    private void initializeBean(BeanDefinition beanDefinition, Object bean) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.beforeBeanInitialize(bean, beanDefinition.getBeanName());
        }
        for (Method method : beanDefinition.getPostConstructMethods()) {
            try {
                method.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException ignore) {
            }
        }
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.afterBeanInitialize(bean, beanDefinition.getBeanName());
        }
    }

    /**
     * 自动装配Bean的依赖
     *
     * @param beanDefinition Bean定义
     * @param bean           Bean实例
     */
    private void autowireBeans(BeanDefinition beanDefinition, Object bean) {
        for (Field field : beanDefinition.getAutowiredFields()) {
            try {
                field.setAccessible(true);
                field.set(bean, getBean(field.getName()));
            } catch (IllegalAccessException ignore) {}
        }
    }

    /**
     * 包装Bean定义
     *
     * @param aClass Bean对应的类
     */
    private void wrapperBean(Class<?> aClass) {
        BeanDefinition beanDefinition = new BeanDefinition(aClass);
        this.beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
    }

    /**
     * 判断是否可以创建Bean
     *
     * @param aClass 类
     * @return 如果可以创建Bean返回true，否则返回false
     */
    private boolean canCreateBean(Class<?> aClass) {
        return aClass.isAnnotationPresent(Componet.class);
    }

    /**
     * 扫描包下的所有类
     *
     * @param packageName 包名
     * @return 类列表
     */
    private List<Class<?>> scanPackage(String packageName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        String path = packageName.replace(".", "/"); // 统一用/作为分隔符
        List<Class<?>> classes = new ArrayList<>();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                if (resource.getProtocol().equals("file")) {
                    // 处理本地文件系统资源（IDE环境）
                    handleFileResource(resource, packageName, classes);
                } else if (resource.getProtocol().equals("jar")) {
                    // 处理JAR包内资源（JAR运行环境）
                    handleJarResource(resource, packageName, classes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("扫描包失败", e);
        }

        return classes;
    }

    /**
     * 处理JAR包中的资源
     *
     * @param resource    资源URL
     * @param packageName 包名
     * @param classes     类列表
     */
    private void handleJarResource(URL resource, String packageName, List<Class<?>> classes) {
        // 解析JAR URL格式：jar:file:/xxx.jar!/{packagePath}
        String jarUrl = resource.getFile();
        String[] jarParts = jarUrl.split("!");
        String jarFilePath = jarParts[0].substring("file:".length()); // 提取JAR文件路径

        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            String packagePath = packageName.replace(".", "/") + "/";

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName.replace("/", ".")
                            .substring(0, entryName.length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException ignore) {}
                }
            }
        } catch (Exception ignore) {}
    }

    /**
     * 处理文件系统中的资源
     *
     * @param resource    资源URL
     * @param packageName 包名
     * @param classes     类列表
     */
    private void handleFileResource(URL resource, String packageName, List<Class<?>> classes) {
        try {
            Path path = Paths.get(resource.toURI());
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".class")) {
                        int i = file.toString().indexOf(packageName.replace(".", File.separator));
                        String className = file.toString()
                                .substring(i, file.toString().length() - ".class".length())
                                .replace(File.separator, ".");
                        try {
                            classes.add(Class.forName(className));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据名称获取Bean实例
     *
     * @param name Bean名称
     * @param <T>  Bean类型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        if (ioc.containsKey(name)) {
            return (T) ioc.get(name);
        }
        if (loadingIoc.containsKey(name)) {
            return (T) loadingIoc.get(name);
        }
        if (beanDefinitionMap.containsKey(name)) {
            return (T) createBean(beanDefinitionMap.get(name));
        }
        return null;
    }

    /**
     * 根据类型获取Bean实例
     *
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    @SuppressWarnings("all")
    public <T> T getBean(Class<T> clazz) {
        T t = loadingIoc.values()
                .stream()
                .filter(bean -> clazz.isAssignableFrom(bean.getClass()))
                .map(bean -> (T) bean)
                .findFirst()
                .orElse(null);
        if (t != null) {
            return t;
        }
        t = ioc.values()
                .stream()
                .filter(bean -> clazz.isAssignableFrom(bean.getClass()))
                .map(bean -> (T)bean)
                .findFirst()
                .orElse(null);
        if (t != null) {
            return t;
        }
        return getBean(String.valueOf(beanDefinitionMap.values()
                .stream()
                .filter(beanDefinition -> clazz.isAssignableFrom(beanDefinition.getBeanType()))
                .map(beanDefinition -> beanDefinition.getBeanName())
                .findFirst()));
    }

    /**
     * 根据类型获取所有匹配的Bean实例
     *
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getBeans(Class<T> clazz) {
        return ioc.values()
                .stream()
                .filter(bean -> clazz.isAssignableFrom(bean.getClass()))
                .map(bean -> (T)bean)
                .toList();
    }
    public String toString() {
        return "ApplicationContext";
    }
}
