package com.tapirheron.spring.mvc.returns.type.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 返回类型处理器接口，定义了处理不同返回类型的规范
 * <p>
 * 不同的返回类型需要实现该接口来处理相应的响应逻辑
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
public interface ReturnTypeHandler {
    /**
     * 处理返回结果
     *
     * @param result 返回结果对象
     * @param req    HTTP请求对象
     * @param resp   HTTP响应对象
     */
    void handle(Object result, HttpServletRequest req, HttpServletResponse resp);
}
