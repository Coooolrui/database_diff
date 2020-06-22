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

    @PostMapping("getTables")
    public List<String> getTables() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.SHOW_TABLE_NOT_VIEW)) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.getFieldName(DataSource.SCHEMA_NAME));
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getTablesV2")
    public List<String> getTablesV2() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.SHOW_TABLE_NOT_VIEW)) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.getFieldName(DataTarget.SCHEMA_NAME));
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getTablesColumns")
    public Map<String, Map<String, Map<ColumnType, Object>>> getTablesColumns() throws SQLException {
        return getTables().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.addTableName(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                log.error(b);
            }
        }, HashMap::putAll);
    }

    @PostMapping("getTablesColumnsV2")
    public Map<String, Map<String, Map<ColumnType, Object>>> getTablesColumnsV2() throws SQLException {
        return getTablesV2().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.addTableName(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                log.error(b);
            }
        }, HashMap::putAll);
    }

    @PostMapping("getDiffTables")
    public Object getDiffTables() throws SQLException {
        return new TreeMap<>(DataDiff.diff(getTablesColumns(), getTablesColumnsV2()));
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
