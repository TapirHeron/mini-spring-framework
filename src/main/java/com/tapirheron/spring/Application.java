package com.tapirheron.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Componet
public class Application {

    @Value("spring.application.name")
    private static String applicationName;

    public static ApplicationContext run(Class<?> clazz, String[] args) {
        log.info("spring应用正在启动");
        ApplicationContext applicationContext = new ApplicationContext(clazz.getPackage().getName(), args);
        log.info("{}启动完成", applicationName);
        applicationContext.getIoc().put("applicationContext", applicationContext);
        return applicationContext;
    }
}
