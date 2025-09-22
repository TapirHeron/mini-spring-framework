package com.tapirheron.spring.framework.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableConfigurationProperties {
    Class<?>[] value(); // 类中加入的属性类
}
