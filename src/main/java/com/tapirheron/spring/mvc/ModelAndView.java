package com.tapirheron.spring.mvc;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tapirheron
 * 标记类，返回本地静态资源
 */
@Data
public class ModelAndView {
    private String view;
    private Map<String, String> context = new HashMap<>();

    public void addContext(String name, String value) {
        context.put(name, value);
    }
}
