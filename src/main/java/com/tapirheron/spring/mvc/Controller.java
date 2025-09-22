package com.tapirheron.spring.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制器注解，用于标记一个类作为MVC模式中的控制器
 * <p>
 * 控制器负责处理用户请求并返回相应的响应
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}
