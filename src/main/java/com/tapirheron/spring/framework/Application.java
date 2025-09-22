package com.tapirheron.spring.framework;

import com.tapirheron.spring.framework.properties.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用启动类，负责启动Spring应用程序
 * <p>
 * 该类提供了应用启动的入口方法，用于初始化应用上下文并启动Spring容器
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Slf4j
@Componet
public class Application {

    /**
     * 应用名称，从配置文件中读取
     */
    @Value("${spring.application.name}")
    private static String applicationName;

    /**
     * 启动Spring应用程序
     *
     * @param clazz 启动类的Class对象
     * @param args  启动参数
     * @return 应用上下文对象
     */
    public static ApplicationContext run(Class<?> clazz, String[] args) {
        log.info("spring应用正在启动");
        ApplicationContext applicationContext = new ApplicationContext(clazz.getPackage().getName(), args);
        log.info("{}启动完成", applicationName);
        applicationContext.getIoc().put("applicationContext", applicationContext);
        return applicationContext;
    }
}
