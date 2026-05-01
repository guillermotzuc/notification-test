package com.gtzuc.projects.notification.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class SimpleController {

    @GetMapping("/hello")
    public String hello(@RequestParam(required=false) String name) {
        return "Hello " + (name != null ? name : "World") +
                " from instance on port " + System.getProperty("server.port");
    }
}
