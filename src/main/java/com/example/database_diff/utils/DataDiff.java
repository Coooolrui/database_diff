package com.example.database_diff.utils;

import com.example.database_diff.enums.ColumnType;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO 新表组装成create语句
 * TODO 更改的字段生成'add/alter'语句
 * TODO 将结果生成sql文件，可执行
 *
 * @Date 2020/6/13 8:53 上午
 * @Created by haoqi
 */
public class DataDiff {

    /**
     * 获取表的对比结果
     * 包含没有添加的表，不同的字段属性
     * @param tables1 source
     * @param tables2 target
     */
    public static Map<String, Object> diffTablesOrViews(Map<String, Map<String, Map<ColumnType, Object>>> tables1,
                                                        Map<String, Map<String, Map<ColumnType, Object>>> tables2) {


        Map<String, Object> diffTables = new HashMap<>();
        Map<String, Map<String, Map<String, Object>>> diffFields = new HashMap<>();

        tables1.forEach((key, value) -> {
            /**
             * 对比缺少的表
             * @param tables1 source
             * @param tables2 target
             */
            if (!tables2.containsKey(key)) {
                diffTables.put(key, value);
                return;
            }

            Map<String, Map<String, Object>> lackField = diffLackField(value, tables2.get(key));
            if (!lackField.isEmpty()) {
                diffFields.put(key, lackField);
            }
        });

        Map<String, Object> map = new HashMap<>();
        map.put("diffFields", new TreeMap<>(diffFields));
        map.put("newTables", new TreeMap<>(diffTables));
        return map;
    }

    /**
     * 对比两张表是否相等
     */
    private static Boolean compareFieldValue(Object value1, Object value2) {
        return (value1 == null && value2 == null) || ((value1 != null && value2 != null) && (value1.equals(value2)));
    }


    /**
     * 对比不同的字段
     *
     * @param table1 source
     * @param table2 target
     * @author: haoqi
     * @date: 2020/8/19 4:17 下午
     */
    public static Map<String, Map<String, Object>> diffLackField(Map<String, Map<ColumnType, Object>> table1,
                                                                 Map<String, Map<ColumnType, Object>> table2) {

        Map<String, Map<String, Object>> diffTable = new HashMap<>();
        table1.forEach((key, value) -> {

            /*
             * 对比缺少的字段
             */
            if (!table2.containsKey(key)) {
                diffTable.put(key, transformField(value));
                return;
            }

            /*
              比对两个表中字段中不同的属性
             */
            Map<ColumnType, Object> field2Value = table2.get(key);
            for (ColumnType fieldType : ColumnType.columnTypes()) {
                Object value1 = value.get(fieldType);
                Object value2 = field2Value.get(fieldType);

                if (!compareFieldValue(value1, value2)) {
                    Map<String, Object> old_new = new HashMap<>();
                    old_new.put("new", value);
                    old_new.put("old", field2Value);
                    diffTable.put(key, old_new);
                    break;
                }
            }

        });
        return diffTable;
    }

    private static Map<String, Map<String, Object>> transform(Map<String, Map<ColumnType, Object>> map) {
        Map<String, Map<String, Object>> hashMap = new HashMap<>();
        map.forEach((a, b) -> hashMap.put(a, transformField(b)));
        return hashMap;
    }

    private static Map<String, Object> transformField(Map<ColumnType, Object> map) {
        Map<String, Object> hashMap = new HashMap<>();
        map.forEach((a, b) -> hashMap.put(a.name(), b));
        return hashMap;
    }

    public static Map<String, Object> diffRoutines(Map<String, String> source, Map<String, String> target) {
        Map<String, Object> diff = new HashMap<>();
        source.forEach((key, value) -> {
            if (!target.containsKey(key)) {
                diff.put(key, value);
                return;
            }
            String targetValue = target.get(key);
            if (!value.equalsIgnoreCase(targetValue)) {
                Map<String, String> map = new HashMap<>();
                map.put("new", value);
                map.put("old", targetValue);
                diff.put(key, map);
            }
        });
        return diff;
    }


}
