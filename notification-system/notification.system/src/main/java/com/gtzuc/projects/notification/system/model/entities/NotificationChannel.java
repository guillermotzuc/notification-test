package com.gtzuc.projects.notification.system.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notification_channel")
@Data
public class NotificationChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

}