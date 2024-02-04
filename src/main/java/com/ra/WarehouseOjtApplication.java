package com.ra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ra")
public class WarehouseOjtApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseOjtApplication.class, args);
    }
}

