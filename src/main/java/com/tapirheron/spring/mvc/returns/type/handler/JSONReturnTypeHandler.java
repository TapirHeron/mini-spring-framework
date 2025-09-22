package com.tapirheron.spring.mvc.returns.type.handler;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JSON返回类型处理器，用于将结果序列化为JSON格式并写入响应
 * <p>
 * 将对象转换为JSON字符串并设置适当的Content-Type响应头
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
public class JSONReturnTypeHandler implements ReturnTypeHandler {
    /**
     * 处理JSON格式的返回结果
     *
     * @param result 返回结果对象
     * @param req    HTTP请求对象
     * @param resp   HTTP响应对象
     */
    @Override
    public void handle(Object result, HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            resp.getWriter().write(JSONObject.toJSONString(result));
        } catch (Exception ignore) {}
    }
}
