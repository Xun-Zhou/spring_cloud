package com.sigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EurekaClientFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientFeignApplication.class, args);
    }

}
