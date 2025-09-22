package com.tapirheron.spring.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 响应体注解，用于标记方法返回值应直接作为HTTP响应体返回
 * <p>
 * 通常用于RESTful API，将返回值序列化为JSON或其他格式直接写入响应体
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
}
