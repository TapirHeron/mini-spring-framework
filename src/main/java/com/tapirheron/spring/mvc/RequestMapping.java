package com.tapirheron.spring.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求映射注解，用于将HTTP请求映射到处理方法
 * <p>
 * 可以标记在类或方法上，用于定义URL路径与处理方法的映射关系
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";
}
