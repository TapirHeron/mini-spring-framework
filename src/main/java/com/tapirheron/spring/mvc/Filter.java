package com.tapirheron.spring.mvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface Filter {
    boolean[] isDoingNext = new boolean[1];
    void doFilter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    default void doNext() {
        isDoingNext[0] = true;
    }
    default boolean isDoingNext() {
        return isDoingNext[0];
    }
}
