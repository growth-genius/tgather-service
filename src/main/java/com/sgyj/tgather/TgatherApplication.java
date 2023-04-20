package com.sgyj.tgather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TgatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgatherApplication.class, args);
    }

}
