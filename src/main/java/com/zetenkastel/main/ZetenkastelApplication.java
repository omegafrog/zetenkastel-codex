package com.zetenkastel.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.zetenkastel")
@EnableScheduling
public class ZetenkastelApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZetenkastelApplication.class, args);
    }
}
