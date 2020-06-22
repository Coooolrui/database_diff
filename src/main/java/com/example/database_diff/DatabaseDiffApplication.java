package com.example.database_diff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class DatabaseDiffApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatabaseDiffApplication.class, args);
    }
}
