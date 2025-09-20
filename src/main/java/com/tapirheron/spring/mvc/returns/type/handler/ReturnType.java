package com.tapirheron.spring.mvc.returns.type.handler;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnType {
    JSON(new JSONReturnTypeHandler()), // 返回json
    TEXT(new TextReturnTypeHandler()), // 返回文本
    LOCAL(new LocalReturnTypeHandler()); // 返回本地文件

    private final ReturnTypeHandler returnTypeHandler;

}
