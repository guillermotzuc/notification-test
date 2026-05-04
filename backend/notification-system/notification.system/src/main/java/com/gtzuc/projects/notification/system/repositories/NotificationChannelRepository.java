package com.gtzuc.projects.notification.system.repositories;

import com.gtzuc.projects.notification.system.model.Channels;
import com.gtzuc.projects.notification.system.model.entities.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, Long> {

    Optional<NotificationChannel> findByName(Channels name);
}