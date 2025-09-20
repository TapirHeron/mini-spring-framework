package com.tapirheron.spring.test.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R {
    private String name;
    private int age;

    @Override
    public String toString() {
        return "name: " + name + ", age: " + age;
    }
}
