package com.gtzuc.projects.notification.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @GetMapping
    public String getAllUsers() {
        return "List of all users";
    }
}
