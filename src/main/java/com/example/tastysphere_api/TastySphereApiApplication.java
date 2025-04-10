package com.example.tastysphere_api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.tastysphere_api.mapper")
public class TastySphereApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TastySphereApiApplication.class, args);
    }

}
