package com.tapirheron.spring.dao;

import com.mysql.cj.protocol.a.MysqlBinaryValueDecoder;
import com.tapirheron.spring.framework.Autowired;
import com.tapirheron.spring.framework.Componet;
import com.tapirheron.spring.framework.PostConstruct;
import com.tapirheron.spring.framework.properties.ConfigurationProperties;
import com.tapirheron.spring.framework.properties.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Componet
@Slf4j
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseConfig {
    private String url;
    private String username = "root";
    private String password = "root";
    private String driverClassName;
    @Autowired
    private MysqlBinaryValueDecoder mysqlBinaryValueDecoder;

    @PostConstruct
    public void volidate() {
        log.info("数据库配置: {}", this);
        log.debug("数据库配置: {}", url);
        if (driverClassName == null ||
                driverClassName.isEmpty() ||
                url == null ||
                url.isEmpty()) {
            log.error("数据库配置错误");
            throw new RuntimeException("数据库配置错误");
        }
        if (mysqlBinaryValueDecoder == null) {
            log.error("数据库驱动未加载");
            throw new RuntimeException("数据库驱动未加载");
        }
    }
}
