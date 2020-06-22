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
    public static String DRIVER_CLASS_NAME;
    public static String URL;
    public static String databaseName;
    public static String USER;
    public static String PASSWORD;

    private static Connection conn = null;
    @Resource
    private Environment environment;

    @PostConstruct
    void init() {
        DRIVER_CLASS_NAME = environment.getProperty("spring.target.datasource.driver-class-name");
        URL = environment.getProperty("spring.target.datasource.url");
        databaseName = environment.getProperty("spring.target.datasource.schema-name");
        USER = environment.getProperty("spring.target.datasource.username");
        PASSWORD = environment.getProperty("spring.target.datasource.password");

        try {
            //1.加载驱动程序
            Class.forName(DRIVER_CLASS_NAME);
            //2. 获得数据库连接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }
}
