package com.tapirheron.spring.mvc.returns.type.handler;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JSONReturnTypeHandler implements ReturnTypeHandler {
    @Override
    public void handle(Object result, HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            resp.getWriter().write(JSONObject.toJSONString(result));
        } catch (Exception ignore) {}
    }
}
