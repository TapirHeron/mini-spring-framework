package com.tapirheron.spring.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自动装配注解，用于标记需要Spring容器自动注入的字段
 * <p>
 * 该注解会根据字段类型自动从容器中查找匹配的Bean并注入
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Autowired {
}
