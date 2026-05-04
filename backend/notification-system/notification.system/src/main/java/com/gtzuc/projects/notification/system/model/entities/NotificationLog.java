package com.gtzuc.projects.notification.system.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {

    @Id
    @Column(name = "message_timestamp")
    private LocalDateTime messageTimestamp;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "topic_id")
    private Long topicId;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "formatted_timestamp")
    private String formattedTimestamp;
}
