package com.tapirheron.spring.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Spring应用启动注解，用于标记Spring应用程序的启动类
 * <p>
 * 标记了此注解的类作为Spring应用的入口点
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringApplication {
}
