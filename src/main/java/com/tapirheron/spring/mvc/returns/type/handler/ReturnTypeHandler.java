package com.tapirheron.spring.mvc.returns.type.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ReturnTypeHandler {
    void handle(Object result, HttpServletRequest req, HttpServletResponse resp);
}
