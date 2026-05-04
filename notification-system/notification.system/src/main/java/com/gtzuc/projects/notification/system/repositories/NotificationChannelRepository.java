package com.gtzuc.projects.notification.system.repositories;

import com.gtzuc.projects.notification.system.model.entities.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, Long> {

    Optional<NotificationChannel> findByName(String name);
}