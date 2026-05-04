package com.gtzuc.projects.notification.system.controller;

import com.gtzuc.projects.notification.system.model.dto.MessageRequestDTO;
import com.gtzuc.projects.notification.system.model.entities.Message;
import com.gtzuc.projects.notification.system.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> createMessage(@Valid @RequestBody MessageRequestDTO requestDTO) {
        try {
            notificationService.createNotification(requestDTO);
            return new ResponseEntity<>("The notification was created", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable Long userId) {
        List<Message> messages = notificationService.getMessagesByUserId(userId);
        return ResponseEntity.ok(messages);
    }
}
