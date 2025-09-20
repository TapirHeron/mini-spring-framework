package com.tapirheron.spring;

public interface BeanPostProcessor {

    default Object beforeBeanInitialize(Object bean, String beanName) {
        return bean;
    }

    default Object afterBeanInitialize(Object bean, String beanName) {
        return bean;
    }
}
