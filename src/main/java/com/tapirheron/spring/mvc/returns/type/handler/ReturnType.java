package com.tapirheron.spring.mvc.returns.type.handler;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回类型枚举，定义了不同的响应返回类型及其处理器
 * <p>
 * 包括JSON、文本和本地文件三种返回类型
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum ReturnType {
    /**
     * JSON返回类型
     */
    JSON(new JSONReturnTypeHandler()),

    /**
     * 文本返回类型
     */
    TEXT(new TextReturnTypeHandler()),

    /**
     * 本地文件返回类型
     */
    LOCAL(new LocalReturnTypeHandler());

    /**
     * 返回类型处理器
     */
    private final ReturnTypeHandler returnTypeHandler;

}
