package com.tapirheron.spring.test.config;

import com.tapirheron.spring.framework.Bean;
import com.tapirheron.spring.framework.properties.Configuration;
import com.tapirheron.spring.framework.properties.EnableConfigurationProperties;


/**
 * @author TapirHeron
 * 添加第三方类注册为bean
 */
@Configuration
@EnableConfigurationProperties({MyProperties.class})
public class TestConfig {
    @Bean("testBean")
    public String testBean() {
        return "test value";
    }
    @Bean
    public String testBean2() {
        return "test value2";
    }

    @Bean("testProperties")
    public String testProperties(MyProperties myProperties) {
        return myProperties.getName();
    }
    @Bean("2")
    public String testSecondProperties(MySecondProperties mySecondProperties) {
        return mySecondProperties.getName();
    }
}
