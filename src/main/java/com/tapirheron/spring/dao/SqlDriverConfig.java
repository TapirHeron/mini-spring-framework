package com.tapirheron.spring.dao;

import com.mysql.cj.protocol.a.MysqlBinaryValueDecoder;
import com.tapirheron.spring.framework.Bean;
import com.tapirheron.spring.framework.properties.Configuration;

@Configuration
public class SqlDriverConfig {

    @Bean
    public MysqlBinaryValueDecoder mysqlBinaryValueDecoder() {
        return new MysqlBinaryValueDecoder();
    }
}
