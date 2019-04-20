package com.sigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServiceClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceClusterApplication.class, args);
    }

}
