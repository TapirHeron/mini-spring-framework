package com.tapirheron.spring.test;

import com.tapirheron.spring.Application;
import com.tapirheron.spring.ApplicationContext;
import com.tapirheron.spring.SpringApplication;
import lombok.Synchronized;

@SpringApplication
public class TestApplication {

    public static void main(String[] args) {
       Application.run(TestApplication.class, args);
    }
}
