package com.tapirheron.spring.framework.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置类注解，用于标记一个类作为Spring容器的配置类
 * <p>
 * 配置类中可以定义多个@Bean方法，用于创建和配置Bean
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
}
