package com.sigure.service;

import org.springframework.stereotype.Component;

@Component
public class FeignServiceError implements FeignService {

    @Override
    public String hello(String name) {
        return "hi " + name + " hello api error";
    }
}
