package com.tapirheron.spring.test.config;

import com.tapirheron.spring.framework.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "my.second")
@Data
public class MySecondProperties {
    private String name;
    private String age;
}
