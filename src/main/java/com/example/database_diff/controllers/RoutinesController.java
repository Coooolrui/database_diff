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
import java.util.HashMap;
import java.util.Map;

/**
 * @author: haoqi
 * @date: 2020/6/22 4:47 下午
 */
@Slf4j
@RestController
@RequestMapping("routines")
public class RoutinesController {

    @PostMapping("getRoutines")
    public Map<String, String> getRoutines() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataSource.getConnection(), SqlUtil.getFieldNameRoutines(DataSource.SCHEMA_NAME))) {
            Map<String, String> tables = new HashMap<>();
            while (rs.next()) {
                String SPECIFIC_NAME = rs.getString(ColumnType.SPECIFIC_NAME.name());
                String ROUTINE_DEFINITION = rs.getString(ColumnType.ROUTINE_DEFINITION.name());
                tables.put(SPECIFIC_NAME, ROUTINE_DEFINITION);
            }
            return tables;
        }
    }

    @PostMapping("getRoutinesV2")
    public Map<String, String> getRoutinesV2() throws SQLException {
        try (ResultSet rs = SqlUtil.getResultSet(DataTarget.getConnection(), SqlUtil.getFieldNameRoutines(DataTarget.SCHEMA_NAME))) {
            Map<String, String> tables = new HashMap<>();
            while (rs.next()) {
                String SPECIFIC_NAME = rs.getString(ColumnType.SPECIFIC_NAME.name());
                String ROUTINE_DEFINITION = rs.getString(ColumnType.ROUTINE_DEFINITION.name());
                tables.put(SPECIFIC_NAME, ROUTINE_DEFINITION);
            }
            return tables;
        }
    }

    @PostMapping("getDiffRoutines")
    public Map<String, Object> getDiffRoutines() throws SQLException {
        return DataDiff.diffRoutines(getRoutines(), getRoutinesV2());
    }
}
