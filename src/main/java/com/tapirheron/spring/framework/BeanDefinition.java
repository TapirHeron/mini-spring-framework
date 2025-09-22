package com.tapirheron.spring.framework;

import com.tapirheron.spring.framework.properties.Value;
import lombok.Data;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Bean定义类，用于封装Bean的元数据信息
 * <p>
 * 包含Bean的类型、名称、构造函数、初始化方法、自动装配字段和配置值字段等信息
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Data
public class BeanDefinition {

    private final Class<?> beanType;
    private final String beanName;
    private final Constructor<?> constructor;
    private final Method[] postConstructMethods;
    private final Field[] autowiredFields;
    private final Field[] configValueFields;


    /**
     * 构造函数，根据给定的类创建Bean定义
     *
     * @param aClass Bean对应的类
     */
    public BeanDefinition(Class<?> aClass) {
        this.beanName = aClass.getAnnotation(Componet.class).name().isEmpty() ?
                Character.toLowerCase(aClass.getSimpleName().charAt(0)) + aClass.getSimpleName().substring(1) :
                aClass.getAnnotation(Componet.class).name();
        this.beanType = aClass;
        try {
            this.constructor = aClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.postConstructMethods = Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                .toArray(Method[]::new);

        this.autowiredFields = Arrays.stream(aClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .toArray(Field[]::new);
        this.configValueFields = Arrays.stream(aClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Value.class))
                .toArray(Field[]::new);
    }

}
