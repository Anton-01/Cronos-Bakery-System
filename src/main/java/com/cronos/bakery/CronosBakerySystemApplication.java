package com.cronos.bakery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CronosBakerySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CronosBakerySystemApplication.class, args);
    }

}
