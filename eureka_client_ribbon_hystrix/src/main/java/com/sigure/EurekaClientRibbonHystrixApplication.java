package com.sigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
public class EurekaClientRibbonHystrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientRibbonHystrixApplication.class, args);
    }

}
