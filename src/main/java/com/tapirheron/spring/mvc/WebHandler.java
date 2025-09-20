package com.tapirheron.spring.mvc;

import com.tapirheron.spring.mvc.returns.type.handler.ReturnType;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class WebHandler {
    private Object controllerBean;
    private Method invokeMethod;
    private ReturnType returnType;


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
