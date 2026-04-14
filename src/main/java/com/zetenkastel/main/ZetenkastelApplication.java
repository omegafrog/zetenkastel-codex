package com.zetenkastel.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zetenkastel")
public class ZetenkastelApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZetenkastelApplication.class, args);
    }
}
