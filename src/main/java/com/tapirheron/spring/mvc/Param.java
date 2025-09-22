package com.tapirheron.spring.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数注解，用于标记方法参数与HTTP请求参数的映射关系
 * <p>
 * 用于从HTTP请求中提取指定名称的参数值并绑定到方法参数上
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String value() default ""; // 参数名称
}
