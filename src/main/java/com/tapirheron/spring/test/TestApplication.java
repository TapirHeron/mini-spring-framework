package com.tapirheron.spring.test;

import com.tapirheron.spring.Application;
import com.tapirheron.spring.ApplicationContext;
import com.tapirheron.spring.SpringApplication;

@SpringApplication
public class TestApplication {

    public static void main(String[] args) {
        ApplicationContext run = Application.run(TestApplication.class, args);
    }
}
