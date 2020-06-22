package com.example.database_diff.enums;

/**
 * @Date 2020/6/13 8:55 上午
 * @Created by haoqi
 */
public enum ColumnType {
    Field, Type, Null, Key, Default, TableName,
    SPECIFIC_NAME,ROUTINE_DEFINITION;

    public static ColumnType[] columnTypes() {
        return new ColumnType[]{Field, Type, Null, Key, Default};
    }
}
