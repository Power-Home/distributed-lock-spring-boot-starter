package com.hompan.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DlockSpringBootStarterTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlockSpringBootStarterTestApplication.class, args);
    }
}

