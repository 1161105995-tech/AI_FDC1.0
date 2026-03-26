package com.smartarchive;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.smartarchive.**.mapper")
public class IntelligentArchiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntelligentArchiveApplication.class, args);
    }
}
