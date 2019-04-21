package com.sigure.service;

import com.sigure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "eureka-client", configuration = FeignConfig.class, fallback = FeignServiceError.class)
public interface FeignService {

    @GetMapping("/hello")
    String hello(String name);
}