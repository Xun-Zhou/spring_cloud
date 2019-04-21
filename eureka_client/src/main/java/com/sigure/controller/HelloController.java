package com.sigure.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${server.port}")
    private Integer port;

    @RequestMapping("/hello")
    public String hello(String name){
        return "hi " + name + " port is " + port;
    }
}
