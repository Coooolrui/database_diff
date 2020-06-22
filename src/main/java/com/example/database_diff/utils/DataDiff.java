package com.example.database_diff.utils;

import com.example.database_diff.enums.ColumnType;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2020/6/13 8:53 上午
 * @Created by haoqi
 */
public class DataDiff {

    /**
     * @param tables1 source
     * @param tables2 target
     */
    public static Map<String, Map<String, Map<String, Object>>> diff(Map<String, Map<String, Map<ColumnType, Object>>> tables1,
                                                                     Map<String, Map<String, Map<ColumnType, Object>>> tables2) {

        //diffTable 中都是 tables2 中不存在的表
        //和应该保存不同的表
        Map<String, Map<String, Map<String, Object>>> diffTable = new HashMap<>();
        tables1.forEach((key, value) -> {
            /**
             * 对比缺少的表
             * 并删除多出来的表
             *
             * @param tables1 source
             * @param tables2 target
             */
            if (!tables2.containsKey(key)) {
                diffTable.put(key, transform(value));
                return;
            }

            Map<String, Map<String, Object>> lackField = diffLackField(value, tables2.get(key));
            if (!lackField.isEmpty()) {
                diffTable.put(key, lackField);
            }
        });
        return diffTable;
    }

//    public static String assembling(Object o, Object o1) {
//        if (o == null) o = "null";
//        if (o1 == null) o1 = "null";
//        return o + "," + o1;
//    }

    private static Boolean compareFieldValue(Object value1, Object value2) {
        return (value1 == null && value2 == null) || ((value1 != null && value2 != null) && (value1.equals(value2)));
    }


    public static Map<String, Map<String, Object>> diffLackField(Map<String, Map<ColumnType, Object>> table1,
                                                                 Map<String, Map<ColumnType, Object>> table2) {

        Map<String, Map<String, Object>> diffTable = new HashMap<>();
        table1.forEach((key, value) -> {
            /**
             * 对比缺少的字段
             * 并删除多出来的字段
             *
             * @param table1 source
             * @param table2 target
             */
            if (!table2.containsKey(key)) {
                diffTable.put(key, transformField(value));
                return;
            }

            /**
             * 对比相同字段的不同属性
             * 首先获取这个表下所有的字段
             * 然后比对两个表中字段中不同的属性
             * 遇到不同的直接保存并跳出循环
             *
             * @param table1 source
             * @param table2 target
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
