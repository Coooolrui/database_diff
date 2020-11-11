package com.example.database_diff.controllers;

import com.example.database_diff.enums.ColumnType;
import com.example.database_diff.utils.DataDiff;
import com.example.database_diff.utils.DataSource;
import com.example.database_diff.utils.DataTarget;
import com.example.database_diff.utils.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @Date 2020/6/12 10:11 上午
 * @Created by haoqi
 */
@Slf4j
@RestController
@RequestMapping("tables")
public class TableController {

    @PostMapping("getSourceTables")
    public List<String> getSourceTables() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.SHOW_TABLE_NOT_VIEW)) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.getFieldName(DataSource.SCHEMA_NAME));
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getTargetTables")
    public List<String> getTargetTables() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.SHOW_TABLE_NOT_VIEW)) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.getFieldName(DataTarget.SCHEMA_NAME));
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getSourceTablesColumns")
    public Map<String, Map<String, Map<ColumnType, Object>>> getSourceTablesColumns() throws SQLException {
        return getSourceTables().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.addTableName(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                log.error(b);
            }
        }, HashMap::putAll);
    }

    @PostMapping("getTargetTablesColumns")
    public Map<String, Map<String, Map<ColumnType, Object>>> getTargetTablesColumns() throws SQLException {
        return getTargetTables().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.addTableName(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                log.error(b);
            }
        }, HashMap::putAll);
    }

    @PostMapping("getDiffTables")
    public Object getDiffTables() throws SQLException {
        TreeMap<String, Object> map = new TreeMap<>(DataDiff.diffTablesOrViews(getSourceTablesColumns(), getTargetTablesColumns()));
        TreeMap diffTables = (TreeMap) map.get("diffFields");
        //key 表名
        //value字段集合
        diffTables.forEach((key, value) -> {

            //fieldsKey 字段名称
            //fieldsValue 属性/值集合
            ((HashMap) value).forEach((fieldsKey, fieldsValue) -> {

                HashMap field = (HashMap) fieldsValue;
                if (field.keySet().size() == 2) {
                    //存在字段差异 key 为 'new' 'old'
                    //source
                    HashMap<ColumnType, String> _new = (HashMap<ColumnType, String>) field.get(SqlUtil.SOURCE);
                    //target
                    HashMap<ColumnType, String> _old = (HashMap<ColumnType, String>) field.get(SqlUtil.TARGET);


                } else {
                    //新字段 key 为'enum ColumnType'
                    HashMap<ColumnType, String> _field = (HashMap<ColumnType, String>) field;

                }
            });
        });
        return map;
    }

    public Map<String, Map<String, Map<ColumnType, Object>>> addTable(ResultSet rs, String tableName) throws SQLException {
        Map<String, Map<String, Map<ColumnType, Object>>> tables = new HashMap<>();

        Map<String, Map<ColumnType, Object>> table = new HashMap<>();
        while (rs.next()) {
            String field = rs.getString(ColumnType.Field.name());

            Map<ColumnType, Object> map = new HashMap<>();
            map.put(ColumnType.TableName, tableName);
            map.put(ColumnType.Field, field);
            map.put(ColumnType.Type, rs.getString(ColumnType.Type.name()));
            map.put(ColumnType.Null, rs.getString(ColumnType.Null.name()));
            map.put(ColumnType.Key, rs.getString(ColumnType.Key.name()));
            map.put(ColumnType.Default, rs.getString(ColumnType.Default.name()));
            map.put(ColumnType.Extra, rs.getString(ColumnType.Extra.name()));

            table.put(field, map);
        }
        tables.put(tableName, table);
        return tables;
    }

}
