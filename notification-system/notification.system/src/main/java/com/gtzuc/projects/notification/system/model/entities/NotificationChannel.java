package com.gtzuc.projects.notification.system.model.entities;

import com.gtzuc.projects.notification.system.model.Channels;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notification_channel")
@Data
public class NotificationChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 100)
    private Channels name;

}