package com.tapirheron.spring.framework;

/**
 * Bean后置处理器接口，用于在Bean初始化前后执行自定义逻辑
 * <p>
 * 实现此接口可以在Bean实例化后、初始化前后对Bean进行处理
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
public interface BeanPostProcessor {

    /**
     * 在Bean初始化之前执行
     *
     * @param bean     Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
    default Object beforeBeanInitialize(Object bean, String beanName) {
        return bean;
    }

    /**
     * 在Bean初始化之后执行
     *
     * @param bean     Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
    default Object afterBeanInitialize(Object bean, String beanName) {
        return bean;
    }
}
