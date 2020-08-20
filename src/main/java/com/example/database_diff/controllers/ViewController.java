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
 * @author: haoqi
 * @date: 2020/6/22 4:47 下午
 */
@Slf4j
@RestController
@RequestMapping("views")
public class ViewController {

    @PostMapping("getViews")
    public List<String> getViews() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.getFieldNameView(DataSource.SCHEMA_NAME))) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.TABLE_NAME);
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getViewsV2")
    public List<String> getViewsV2() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.getFieldNameView(DataTarget.SCHEMA_NAME))) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.TABLE_NAME);
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getViewsColumns")
    public Map<String, Map<String, Map<ColumnType, Object>>> getViewsColumns() throws SQLException {
        return getViews().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.addTableName(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                log.error(b);
            }
        }, HashMap::putAll);
    }

    @PostMapping("getViewsColumnsV2")
    public Map<String, Map<String, Map<ColumnType, Object>>> getViewsColumnsV2() throws SQLException {
        return getViewsV2().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.addTableName(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                log.error(b);
            }
        }, HashMap::putAll);
    }

    @PostMapping("getDiffViews")
    public Object getDiffViews() throws SQLException {
        return new TreeMap<>(DataDiff.diffTablesOrViews(getViewsColumns(), getViewsColumnsV2()));
    }

    public Map<String, Map<String, Map<ColumnType, Object>>> addTable(ResultSet rs, String tableName) throws SQLException {
        Map<String, Map<String, Map<ColumnType, Object>>> tables = new HashMap<>();

        Map<String, Map<ColumnType, Object>> table = new HashMap<>();
        while (rs.next()) {
            String field = rs.getString(ColumnType.Field.name());
            String type = rs.getString(ColumnType.Type.name());
            String aNull = rs.getString(ColumnType.Null.name());
            String key = rs.getString(ColumnType.Key.name());
            String aDefault = rs.getString(ColumnType.Default.name());

            Map<ColumnType, Object> map = new HashMap<>();
            map.put(ColumnType.TableName, tableName);
            map.put(ColumnType.Field, field);
            map.put(ColumnType.Type, type);
            map.put(ColumnType.Null, aNull);
            map.put(ColumnType.Key, key);
            map.put(ColumnType.Default, aDefault);

            table.put(field, map);
        }
        tables.put(tableName, table);
        return tables;
    }

}
