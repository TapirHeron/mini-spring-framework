package com.tapirheron.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class BeanDefinition {

    private final Class<?> beanType;
    private final String beanName;
    private final Constructor<?> constructor;
    private final Method[] postConstructMethods;
    private final Field[] autowiredFields;
    private final Field[] configValueFields;


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

    public Constructor<?> getConstructor() {
        return constructor;
    }
    public String getBeanName() {
        return beanName;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public Method[] getPostConstructMethods() {
        return postConstructMethods;
    }

    public Field[] getAutowiredFields() {
        return autowiredFields;
    }

    public Field[] getConfigValueFields() {
        return configValueFields;
    }
}
