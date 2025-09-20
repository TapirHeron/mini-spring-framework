package com.tapirheron.spring;

import lombok.Data;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
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

@Data
public class ApplicationContext {

    private final Map<String, Object> ioc;
    private final Map<String, Object> loadingIoc;
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final List<BeanPostProcessor> beanPostProcessors;
    private final Map<String, String> configValueMap;


    public ApplicationContext(String packageName, String[] args) {
        ioc = new HashMap<>();
        loadingIoc = new HashMap<>();
        beanDefinitionMap = new HashMap<>();
        beanPostProcessors = new ArrayList<>();
        configValueMap = new HashMap<>();
        loadingIoc.put("applicationContext", this);
        initContext("com.tapirheron.spring");
        initContext(packageName);
        loadingIoc.clear();
    }

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

    private void initContext(String packageName) {
        loadConfigValueMap();
        scanPackage(packageName).stream()
                .peek(this::initConfigurationBean)
                .filter(this::canCreateBean)
                .forEach(this::wrapperBean);
        initBeanPostProcessors();
        beanDefinitionMap.values()
                .forEach(this::createBean);

    }

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
                        configClassInstance = aClass.getConstructor().newInstance();
                        method.setAccessible(true);
                        Object beanInstance = method.invoke(configClassInstance);
                        ioc.put(beanName, beanInstance);
                    } catch (Exception ignore) {}
                });
    }

    private void loadConfigValueMap() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("application.properties"))))) {
            reader.lines()
                    .forEach(line -> {
                        String[] split = line.split("=");
                        if (split.length == 2) {
                            configValueMap.put(split[0], split[1]);
                        }
                    });
        } catch (Exception ignore) {}
    }

    private Object createBean(BeanDefinition beanDefinition) {
       if (ioc.containsKey(beanDefinition.getBeanName())) {
           return ioc.get(beanDefinition.getBeanName());
       }
        return doCreateBean(beanDefinition);
    }

    private Object doCreateBean(BeanDefinition beanDefinition) {
        Object bean;

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
        ioc.put(beanDefinition.getBeanName(), loadingIoc.remove(beanDefinition.getBeanName()));
        return bean;
    }


    private void injectConfigValue(BeanDefinition beanDefinition, Object bean) {
        for (Field field : beanDefinition.getConfigValueFields()) {
            field.setAccessible(true);
            String configKey = field.getAnnotation(Value.class).value();
            String value = configValueMap.get(configKey.isBlank() ? field.getName() : configKey);
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

    private void autowireBeans(BeanDefinition beanDefinition, Object bean) {
        for (Field field : beanDefinition.getAutowiredFields()) {
            try {
                field.setAccessible(true);
                field.set(bean, getBean(field.getName()));
            } catch (IllegalAccessException ignore) {
            }
        }
    }

    private void wrapperBean(Class<?> aClass) {
        BeanDefinition beanDefinition = new BeanDefinition(aClass);
        this.beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
    }

    private boolean canCreateBean(Class<?> aClass) {
        return aClass.isAnnotationPresent(Componet.class);
    }

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


    public <T> T getBean(String name) {
        if (ioc.containsKey(name)) {
            return (T) ioc.get(name);
        }
        if (loadingIoc.containsKey(name)) {
            return (T) loadingIoc.get(name);
        }
        if (beanDefinitionMap.containsKey(name)) {
            return (T)createBean(beanDefinitionMap.get(name));
        }
        return null;
    }
    public <T> T getBean(Class<T> clazz) {
        return ioc.values()
                .stream()
                .filter(bean -> clazz.isAssignableFrom(bean.getClass()))
                .map(bean -> (T)bean)
                .findFirst()
                .orElseGet(null);
    }

    public <T> List<T> getBeans(Class<T> clazz) {
        return ioc.values()
                .stream()
                .filter(bean -> clazz.isAssignableFrom(bean.getClass()))
                .map(bean -> (T)bean)
                .toList();
    }
}
