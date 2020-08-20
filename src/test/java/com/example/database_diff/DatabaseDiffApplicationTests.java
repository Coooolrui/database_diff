package com.example.database_diff;

import com.example.database_diff.controllers.TableController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
class DatabaseDiffApplicationTests {

    @Test
    void contextLoads() throws SQLException {
        TableController tableController = new TableController();
       System.out.println(tableController.getDiffTables());
        //System.out.println(tableController.getTablesV2());
    }

}
