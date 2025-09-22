package com.tapirheron.spring.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件注解，用于标记一个类作为Spring容器管理的组件
 * <p>
 * 该注解标记的类会被自动扫描并注册为Bean
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Componet {
    String name() default "";
}
