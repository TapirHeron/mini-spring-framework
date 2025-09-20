package com.tapirheron.spring.test.filter;

import com.tapirheron.spring.Componet;
import com.tapirheron.spring.mvc.Filter;
import com.tapirheron.spring.mvc.returns.type.handler.WebFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/hello/*")
@Componet
public class EncodeFilter implements Filter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doNext();
    }
}
