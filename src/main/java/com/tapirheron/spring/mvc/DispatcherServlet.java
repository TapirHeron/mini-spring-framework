package com.tapirheron.spring.mvc;

import com.tapirheron.spring.framework.ApplicationContext;
import com.tapirheron.spring.framework.Autowired;
import com.tapirheron.spring.framework.BeanPostProcessor;
import com.tapirheron.spring.framework.Componet;
import com.tapirheron.spring.framework.PostConstruct;
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
import java.util.regex.Pattern;

/**
 * 请求分发Servlet，负责处理所有HTTP请求并分发给相应的处理器
 * <p>
 * 该类是MVC框架的核心组件，负责请求路由、参数解析、方法调用和响应处理
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Getter
@Componet
@Slf4j
@SuppressWarnings("all")
public class DispatcherServlet extends HttpServlet implements BeanPostProcessor {

    /**
     * 处理器映射表，存储URL与处理器的映射关系
     */
    private final Map<String, WebHandler> handlerMap = new HashMap<>();

    /**
     * 过滤器列表
     */
    private List<Filter> filters = new ArrayList<>();

    /**
     * 应用上下文，用于获取Bean实例
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 初始化过滤器链
     *
     */
    @PostConstruct
    public void initChain() {
        filters = applicationContext.getBeans(Filter.class);
    }

    /**
     * 处理HTTP请求
     *
     * @param req  HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        log.info("收到请求的url是 {}", req.getRequestURI());
        for (Filter filter : filters) {
            if (!matchUri(filter, req)) {
//                log.info("过滤器{}不匹配当前请求{}", filter.getClass().getName(), req.getRequestURI());
                continue;
            }
//            log.info("开始执行过滤器 {}", filter.getClass().getName());
            filter.doFilter(req, resp);
            if (!filter.isDoingNext()) {
//                log.info("过滤器{}停止执行下一个过滤器", filter.getClass().getName());
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
//        log.info("该请求的参数是 {}", args);
        try {
            Object result;
            Method invokeMethod = handler.getInvokeMethod();
            // 判断事务处理
            if (invokeMethod.isAnnotationPresent(Transactionnal.class)) {
                synchronized (invokeMethod) {
                    result = invokeMethod.invoke(handler.getControllerBean(), args);
                }
            } else {
                result = invokeMethod.invoke(handler.getControllerBean(), args);
            }
            handler.getReturnType().getReturnTypeHandler().handle(result, req, resp);
        } catch (Exception ignore) {}

    }

    /**
     * 匹配过滤器与请求URI
     *
     * @param filter 过滤器
     * @param req    HTTP请求对象
     * @return 如果匹配返回true，否则返回false
     */
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

    /**
     * 解析方法参数
     *
     * @param method 处理方法
     * @param req    HTTP请求对象
     * @return 参数数组
     */
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

    /**
     * 查找请求对应的处理器
     *
     * @param req HTTP请求对象
     * @return 对应的Web处理器
     */
    private WebHandler findHandler(HttpServletRequest req) {
        return handlerMap.get(req.getRequestURI());
    }

    /**
     * Bean初始化后处理，用于注册控制器方法
     *
     * @param bean     Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
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
