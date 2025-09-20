package com.tapirheron.spring.mvc.returns.type.handler;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TextReturnTypeHandler implements ReturnTypeHandler{
    @Override
    public void handle(Object result, HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            resp.getWriter().write(result.toString());
        } catch (Exception ignore) {}
    }
}
