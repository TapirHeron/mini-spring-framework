package com.tapirheron.spring.mvc.returns.type.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 文本返回类型处理器，用于将结果转换为文本格式并写入响应
 * <p>
 * 将对象转换为字符串并设置适当的Content-Type响应头
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
public class TextReturnTypeHandler implements ReturnTypeHandler{
    /**
     * 处理文本格式的返回结果
     *
     * @param result 返回结果对象
     * @param req    HTTP请求对象
     * @param resp   HTTP响应对象
     */
    @Override
    public void handle(Object result, HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            resp.getWriter().write(result.toString());
        } catch (Exception ignore) {}
    }
}
