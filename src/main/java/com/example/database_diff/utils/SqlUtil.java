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
    public static final String SHOW_TABLE = "SHOW TABLES";
    public static final String SHOW_TABLE_NOT_VIEW = "SHOW FULL TABLES WHERE Table_type != 'VIEW'";
    public static final String SHOW_COLUMN = "SHOW COLUMNS FROM ";

    public static Statement getStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    public static ResultSet getResultV1() throws SQLException {
        return getStatement(DbUtil.getConnection()).executeQuery(SHOW_TABLE_NOT_VIEW);
    }

    public static ResultSet getResultV2() throws SQLException {
        return getStatement(DbUtilV2.getConnection()).executeQuery(SHOW_TABLE_NOT_VIEW);
    }

    public static String tableName() {
        return "Tables_in_" + DbUtil.databaseName;
    }

    public static String tableName2() {
        return "Tables_in_" + DbUtilV2.databaseName;
    }

}
