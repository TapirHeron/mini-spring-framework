package com.tapirheron.spring.mvc;

import com.tapirheron.spring.framework.Autowired;
import com.tapirheron.spring.framework.Componet;
import com.tapirheron.spring.framework.PostConstruct;
import com.tapirheron.spring.framework.properties.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.logging.LogManager;

/**
 * Tomcat服务器类，用于启动和管理内嵌的Tomcat服务器
 * <p>
 * 该类负责配置和启动Tomcat服务器，并将请求分发给DispatcherServlet处理
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
@Slf4j
@Componet
@SuppressWarnings("all")
public class TomcatServer {

    /**
     * 服务器端口，默认为8080
     */
    @Value("${server.port}")
    private int port = 8080;

    /**
     * 请求分发Servlet
     */
    @Autowired
    private DispatcherServlet dispatcherServlet;

    /**
     * 启动Tomcat服务器
     */
    @PostConstruct
    public void start() {
        log.info("启动Tomcat");
        // 重置日志
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // 创建Tomcat实例
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        // 创建Context上下文
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        // 添加Servlet
        tomcat.addServlet(contextPath, "dispatcherServlet", dispatcherServlet);

        // 设置上下文参数
        context.addServletMappingDecoded("/*", "dispatcherServlet");
        try {
            tomcat.start();
            log.info("Tomcat启动，正在监听{}", port);
        } catch (LifecycleException ignore) {}
    }
}
