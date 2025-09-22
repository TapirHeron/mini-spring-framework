package com.tapirheron.spring.mvc;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 模型视图类，用于封装视图名称和模型数据
 * <p>
 * 该类用于MVC模式中，将模型数据和视图信息一起返回给前端
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Data
public class ModelAndView {
    /**
     * 视图名称
     */
    private String view;

    /**
     * 上下文数据，用于在视图中展示的模型数据
     */
    private Map<String, String> context = new HashMap<>();

    /**
     * 添加上下文数据
     *
     * @param name  数据名称
     * @param value 数据值
     */
    public void addContext(String name, String value) {
        context.put(name, value);
    }
}
