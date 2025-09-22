package com.tapirheron.spring.dao;

import com.tapirheron.spring.framework.Autowired;
import com.tapirheron.spring.framework.Componet;
import com.tapirheron.spring.framework.Order;
import com.tapirheron.spring.framework.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Data
@Componet
@Order(100)
@Slf4j
public class MySqlSessionFactory {
    @Autowired
    private DatabaseConfig databaseConfig;
    @Autowired
    private MySqlSessionFactoryInvocationHandler mySqlSessionFactoryInvocationHandler;

    private Connection connection;
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{clazz},
                mySqlSessionFactoryInvocationHandler);
    }

    /**
     * 初始化数据库连接
     * @throws SQLException
     */
    @PostConstruct
    public void init() throws SQLException {
        this.connection = DriverManager.getConnection(databaseConfig.getUrl(),
                databaseConfig.getUsername(),
                databaseConfig.getPassword());
        log.info("初始化MySqlSessionFactory{}", connection == null);
    }

}
