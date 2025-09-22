package com.tapirheron.spring.test;

import com.tapirheron.spring.framework.Application;
import com.tapirheron.spring.framework.ApplicationContext;
import com.tapirheron.spring.framework.SpringApplication;

@SpringApplication
public class TestApplication {

    public static void main(String[] args) {

        ApplicationContext run = Application.run(TestApplication.class, args);
    }
}
