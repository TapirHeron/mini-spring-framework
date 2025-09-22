package com.tapirheron.spring.mvc.returns.type.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Web过滤器注解，用于标记一个类作为HTTP请求的过滤器
 * <p>
 * 该注解可以指定过滤器应用的URI模式，支持通配符匹配
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebFilter {
    String value() default ""; // 过滤的请求uri
}
