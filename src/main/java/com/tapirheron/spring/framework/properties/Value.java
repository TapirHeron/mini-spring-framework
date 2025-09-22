package com.tapirheron.spring.framework.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 值注入注解，用于将配置文件中的属性值注入到字段中
 * <p>
 * 支持从配置文件中读取属性值并注入到标记的字段中
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    String value() default  ""; // 属性值
}
