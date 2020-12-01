package com.example.database_diff.controllers;

import com.example.database_diff.enums.ColumnType;
import com.example.database_diff.utils.DataDiff;
import com.example.database_diff.utils.DataSource;
import com.example.database_diff.utils.DataTarget;
import com.example.database_diff.utils.SqlUtil;
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
@RestController
@RequestMapping("views")
public class ViewController {

    @PostMapping("getSourceViews")
    public List<String> getSourceViews() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.getFieldNameView(DataSource.SCHEMA_NAME))) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.TABLE_NAME);
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getTargetViews")
    public List<String> getTargetViews() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.getFieldNameView(DataTarget.SCHEMA_NAME))) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tables_in_tdasapp = rs.getString(SqlUtil.TABLE_NAME);
                tables.add(tables_in_tdasapp);
            }
            return tables;
        }
    }

    @PostMapping("getSourceViewsColumns")
    public Map<String, Map<String, Map<ColumnType, Object>>> getSourceViewsColumns() throws SQLException {
        return getSourceViews().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.getColumns(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }, HashMap::putAll);
    }

    @PostMapping("getTargetViewsColumns")
    public Map<String, Map<String, Map<ColumnType, Object>>> getTargetViewsColumns() throws SQLException {
        return getTargetViews().stream().collect(HashMap::new, (a, b) -> {
            try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.getColumns(b))) {
                a.putAll(addTable(rs, b));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }, HashMap::putAll);
    }

    /**
     * TODO 修改视图的处理方式 存储过程应该也能这样改
     * 1. 有差异的视图只需要先删除再添加
     * 2. 新视图直接添加
     *
     * @return
     * @throws SQLException
     */
    @PostMapping("getDiffViews")
    public Object getDiffViews() throws SQLException {
        return new TreeMap<>(DataDiff.diffTablesOrViews(getSourceViewsColumns(), getTargetViewsColumns()));
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

            table.put(field, map);
        }
        tables.put(tableName, table);
        return tables;
    }

}
