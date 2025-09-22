package com.tapirheron.spring.mvc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器接口，用于定义请求处理前后的过滤逻辑
 * <p>
 * 实现该接口可以对HTTP请求进行预处理和后处理操作
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
public interface Filter {
    /**
     * 是否继续执行下一个过滤器的标志
     */
    boolean[] isDoingNext = new boolean[1];

    /**
     * 执行过滤逻辑
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    void doFilter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    /**
     * 继续执行下一个过滤器
     */
    default void doNext() {
        isDoingNext[0] = true;
    }

    /**
     * 判断是否继续执行下一个过滤器
     *
     * @return 如果继续执行返回true，否则返回false
     */
    default boolean isDoingNext() {
        return isDoingNext[0];
    }
}
