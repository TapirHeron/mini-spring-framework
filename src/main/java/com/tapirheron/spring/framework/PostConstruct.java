package com.tapirheron.spring.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 初始化注解，用于标记Bean初始化完成后需要执行的方法
 * <p>
 * 被此注解标记的方法会在Bean的所有属性设置完成后自动执行
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}
