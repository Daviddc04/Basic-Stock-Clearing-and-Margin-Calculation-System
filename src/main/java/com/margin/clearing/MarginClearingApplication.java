package com.margin.clearing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MarginClearingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarginClearingApplication.class, args);
    }
}
