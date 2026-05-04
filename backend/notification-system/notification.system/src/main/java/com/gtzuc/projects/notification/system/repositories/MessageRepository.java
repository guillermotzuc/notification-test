package com.gtzuc.projects.notification.system.repositories;

import com.gtzuc.projects.notification.system.model.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByUserId(Long userId);

    List<Message> findByTopicId(Long topicId);

    List<Message> findByChannelId(Long channelId);
}