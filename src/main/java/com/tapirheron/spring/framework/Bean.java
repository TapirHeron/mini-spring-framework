package com.tapirheron.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Bean注解，用于标记配置类中的方法，将其返回值注册为Spring容器中的Bean
 * <p>
 * 被此注解标记的方法会在容器启动时被调用，其返回值会被注册为Bean
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String value() default ""; // Bean的名称
}
