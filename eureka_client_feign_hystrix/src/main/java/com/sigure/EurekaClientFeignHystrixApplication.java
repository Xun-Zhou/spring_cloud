package com.sigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
@EnableFeignClients
public class EurekaClientFeignHystrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientFeignHystrixApplication.class, args);
    }

}
