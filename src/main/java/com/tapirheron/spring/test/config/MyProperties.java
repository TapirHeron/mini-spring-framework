package com.tapirheron.spring.test.config;

import com.tapirheron.spring.framework.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "my")
@Data
public class MyProperties {
    private String name;
    private String age;
}
