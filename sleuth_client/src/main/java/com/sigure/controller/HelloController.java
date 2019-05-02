package com.sigure.controller;

import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${server.port}")
    private String port;

    @Autowired
    private Tracer tracer;

    @GetMapping("/hello")
    public String hello(@RequestParam String name){
        tracer.currentSpan().tag("name", name);
        return "hello " + name + " port is " + port;
    }
}
