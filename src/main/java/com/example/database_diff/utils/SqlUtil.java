package com.example.database_diff.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Date 2020/6/22 2:20 下午
 * @Created by haoqi
 */
public class SqlUtil {
    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String SHOW_TABLE = "SHOW TABLES";
    public static final String SHOW_TABLE_NOT_VIEW = "SHOW FULL TABLES WHERE Table_type != 'VIEW'";
    public static final String SHOW_VIEW_BEGIN = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = '";
    public static final String SHOW_VIEW_END = "' ORDER BY TABLE_NAME ASC";
    public static final String SHOW_COLUMN = "SHOW COLUMNS FROM ";

    public static Statement getStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    public static ResultSet getResultSet(Connection connection, String sql) throws SQLException {
        return getStatement(connection).executeQuery(sql);
    }

    public static String getFieldName(String schemaName) {
        return "Tables_in_" + schemaName;
    }

    public static String getFieldNameView(String schemaName) {
        return SHOW_VIEW_BEGIN + schemaName + SHOW_VIEW_END;
    }
}
