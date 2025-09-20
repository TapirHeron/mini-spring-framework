package com.tapirheron.spring.mvc;

import com.tapirheron.spring.Autowired;
import com.tapirheron.spring.Componet;
import com.tapirheron.spring.PostConstruct;
import com.tapirheron.spring.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.logging.LogManager;

@Slf4j
@Componet
public class TomcatServer {

    @Value("server.port")
    private int port = 8080;

    @Autowired
    private DispatcherServlet dispatcherServlet;

    @PostConstruct
    public void start() {
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
        } catch (LifecycleException ignore) {
        }

        log.info("Tomcat启动，正在监听{}", port);
    }
}
