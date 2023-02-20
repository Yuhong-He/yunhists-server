package com.example.yunhists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YunhistsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunhistsServerApplication.class, args);
    }

}
