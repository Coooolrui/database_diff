package org.hq;

import org.hq.utils.DataConnect;

import java.sql.SQLException;


public class ApplicationMain {
    public static void main(String[] args) {
        System.out.println("正在操作，请等待...\n");
        DataConnect.init();
        try {
            System.out.println(Table.getDiffTables());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("\n操作完毕");
        System.exit(0);
    }
}
