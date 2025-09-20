package com.tapirheron.spring.mvc;

import com.alibaba.fastjson2.JSON;
import com.tapirheron.spring.ApplicationContext;
import com.tapirheron.spring.Autowired;
import com.tapirheron.spring.BeanPostProcessor;
import com.tapirheron.spring.Componet;
import com.tapirheron.spring.PostConstruct;
import com.tapirheron.spring.mvc.returns.type.handler.WebFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Componet
@Slf4j
public class DispatcherServlet extends HttpServlet implements BeanPostProcessor {

    private final Map<String, WebHandler> handlerMap = new HashMap<>();
    private List<Filter> filters = new ArrayList<>();
    @Autowired
    private ApplicationContext applicationContext;
    @PostConstruct
    public void initChain() throws ServletException {
        filters = applicationContext.getBeans(Filter.class);
    }
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        log.info("收到请求的url是 {}", req.getRequestURI());
        for (Filter filter : filters) {
            if (!matchUri(filter, req)) {
                log.info("过滤器{}不匹配当前请求{}", filter.getClass().getName(), req.getRequestURI());
                continue;
            }
            log.info("开始执行过滤器 {}", filter.getClass().getName());
            filter.doFilter(req, resp);
            if (!filter.isDoingNext()) {
                log.info("过滤器{}停止执行下一个过滤器", filter.getClass().getName());
                break;
            }

        }
        WebHandler handler = findHandler(req);
        if (handler == null) {
            resp.setContentType("text/html");
            resp.getWriter().write("<p>你的请求出错</p>" + req.getRequestURL());
            return;
        }
        Object[] args = resolveArgs(handler.getInvokeMethod(), req);
        log.info("该请求的参数是 {}", args);
        try {
            Object result = handler.getInvokeMethod().invoke(handler.getControllerBean(), args);
            handler.getReturnType().getReturnTypeHandler().handle(result, req, resp);
        } catch (Exception ignore) {}

    }

    private boolean matchUri(Filter filter, HttpServletRequest req) {
        if (!filter.getClass().isAnnotationPresent(WebFilter.class)) {
            return false;
        }
        String filterUri = filter.getClass().getAnnotation(WebFilter.class).value();
        String regex = filterUri.replace("*", ".*");
        regex = "^" + regex + "$";

        Pattern patternObject = Pattern.compile(regex);
        return patternObject.matcher(req.getRequestURI()).matches();
    }

    private Object[] resolveArgs(Method method, HttpServletRequest req) {
        Object[] args = new Object[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        int index = 0;
        for (Parameter parameter : parameters) {
            Param param = parameter.getAnnotation(Param.class);
            if (param == null) {
                continue;
            }
            String paramName = param.value().isBlank() ? parameter.getName() : param.value();
            Class<?> type = parameter.getType();
            if (int.class.isAssignableFrom(type)) {
                args[index++] = Integer.parseInt(req.getParameter(paramName));
            } else if (float.class.isAssignableFrom(type)) {
                args[index++] = Float.parseFloat(req.getParameter(paramName));
            } else if (String.class.isAssignableFrom(type)){
                args[index++] = req.getParameter(paramName);
            } else {
                args[index++] = null;
            }
        }
        return args;
    }

    private WebHandler findHandler(HttpServletRequest req) {
        return handlerMap.get(req.getRequestURI());
    }

    @Override
    public Object afterBeanInitialize(Object bean, String beanName) {
        if (!bean.getClass().isAnnotationPresent(Controller.class)) {
            return bean;
        }
        String urlString = bean.getClass().isAnnotationPresent(RequestMapping.class) ?
                bean.getClass().getAnnotation(RequestMapping.class).value() :
                "";
        Arrays.stream(bean.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .forEach(method -> {
                    String fullUrl = urlString.concat(method.getAnnotation(RequestMapping.class).value());
                    if (handlerMap.containsKey(fullUrl)) {
                        throw new RuntimeException("不能定义相同的" + fullUrl + "到不同的方法");
                    }
                    handlerMap.put(fullUrl, new WebHandler(bean, method));
                });
        return bean;
    }
}
