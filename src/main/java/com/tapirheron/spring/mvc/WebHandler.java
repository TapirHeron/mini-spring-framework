package com.tapirheron.spring.mvc;

import com.tapirheron.spring.mvc.returns.type.handler.ReturnType;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * Web处理器类，用于封装Web请求的处理信息
 * <p>
 * 包含控制器Bean实例、处理方法和返回类型等信息
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Data
public class WebHandler {
    /**
     * 控制器Bean实例
     */
    private Object controllerBean;

    /**
     * 处理方法
     */
    private Method invokeMethod;

    /**
     * 返回类型
     */
    private ReturnType returnType;


    /**
     * 构造函数，根据控制器Bean和处理方法创建Web处理器
     *
     * @param controllerBean 控制器Bean实例
     * @param invokeMethod   处理方法
     */
    public WebHandler(Object controllerBean, Method invokeMethod) {
        this.controllerBean = controllerBean;
        this.invokeMethod = invokeMethod;
        if (invokeMethod.isAnnotationPresent(ResponseBody.class)) {
            this.returnType = ReturnType.JSON;
        } else if (invokeMethod.getReturnType() == ModelAndView.class) {
            this.returnType = ReturnType.LOCAL;
        } else {
            this.returnType = ReturnType.TEXT;
        }
    }

}
