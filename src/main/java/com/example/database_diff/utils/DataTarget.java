package com.example.database_diff.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Date 2020/6/12 10:14 上午
 * @Created by haoqi
 */
@Configuration
public class DataTarget {
    public static String URL;
    public static String SCHEMA_NAME;
    public static String USER;
    public static String PASSWORD;

    private static Connection conn = null;
    @Resource
    private Environment environment;

    @PostConstruct
    void init() {
        URL = environment.getProperty("spring.target.datasource.url");
        SCHEMA_NAME = environment.getProperty("spring.target.datasource.schema-name");
        USER = environment.getProperty("spring.target.datasource.username");
        PASSWORD = environment.getProperty("spring.target.datasource.password");

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }
}
