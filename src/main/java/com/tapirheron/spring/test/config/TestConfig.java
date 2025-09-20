package com.tapirheron.spring.test.config;

import com.tapirheron.spring.Bean;
import com.tapirheron.spring.Configuration;


/**
 * @author TapirHeron
 * 添加第三方类注册为bean
 */
@Configuration
public class TestConfig {
    @Bean("testBean")
    public String testBean() {
        return "test value";
    }
    @Bean
    public String testBean2() {
        return "test value2";
    }
}
