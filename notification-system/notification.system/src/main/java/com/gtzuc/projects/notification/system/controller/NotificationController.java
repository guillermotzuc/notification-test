package com.gtzuc.projects.notification.system.controller;

import com.gtzuc.projects.notification.system.model.dto.MessageRequestDTO;
import com.gtzuc.projects.notification.system.model.dto.MessageResponseDTO;
import com.gtzuc.projects.notification.system.model.entities.NotificationLog;
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
    public ResponseEntity<MessageResponseDTO> createMessage(@Valid @RequestBody MessageRequestDTO requestDTO) {
        try {
            notificationService.createNotification(requestDTO);
            return new ResponseEntity<>(new MessageResponseDTO("The notification was sent"), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new MessageResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationLog>> getMessagesByUser(@PathVariable Long userId) {
        List<NotificationLog> messages = notificationService.getMessagesByUserId(userId);
        return ResponseEntity.ok(messages);
    }
}
