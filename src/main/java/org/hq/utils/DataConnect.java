package org.hq.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataConnect {

    public static void init() {
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = DataSource.class.getClassLoader().getResourceAsStream("application.yml");

        Map<String, Object> env = yaml.load(resourceAsStream);

        LinkedHashMap datasource = (LinkedHashMap) ((LinkedHashMap) env.get("source")).get("datasource");
        DataSource.URL = datasource.get("url").toString();
        DataSource.SCHEMA_NAME = datasource.get("schema-name").toString();
        DataSource.USER = datasource.get("username").toString();
        DataSource.PASSWORD = datasource.get("password").toString();

        try {
            DataSource.conn = DriverManager.getConnection(DataSource.URL, DataSource.USER, DataSource.PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        datasource = (LinkedHashMap) ((LinkedHashMap) env.get("target")).get("datasource");
        DataTarget.URL = datasource.get("url").toString();
        DataTarget.SCHEMA_NAME = datasource.get("schema-name").toString();
        DataTarget.USER = datasource.get("username").toString();
        DataTarget.PASSWORD = datasource.get("password").toString();
        try {
            DataTarget.conn = DriverManager.getConnection(DataTarget.URL, DataTarget.USER, DataTarget.PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class DataSource {
        public static String URL;
        public static String SCHEMA_NAME;
        public static String USER;
        public static String PASSWORD;

        private static Connection conn = null;

        public static Connection getConnection() {
            return conn;
        }
    }

    public static class DataTarget {
        public static String URL;
        public static String SCHEMA_NAME;
        public static String USER;
        public static String PASSWORD;

        private static Connection conn = null;

        public static Connection getConnection() {
            return conn;
        }
    }
}
