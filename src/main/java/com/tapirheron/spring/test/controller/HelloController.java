package com.tapirheron.spring.test.controller;

import com.tapirheron.spring.Componet;
import com.tapirheron.spring.mvc.Controller;
import com.tapirheron.spring.mvc.ModelAndView;
import com.tapirheron.spring.mvc.Param;
import com.tapirheron.spring.mvc.RequestMapping;

@Controller
@RequestMapping("/hello")
@Componet
public class HelloController {
    @RequestMapping("/hello")
    public R hello2(@Param("name") String name) {
        return new R(name, 18);
    }
    @RequestMapping("/modelAndView")
    public ModelAndView modelAndView(@Param("name") String name) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("index.html");
        modelAndView.addContext("name", name);
        return modelAndView;
    }
}
