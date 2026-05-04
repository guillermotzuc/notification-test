package com.gtzuc.projects.notification.system.service;

import com.gtzuc.projects.notification.system.model.dto.MessageRequestDTO;
import com.gtzuc.projects.notification.system.model.entities.Message;
import com.gtzuc.projects.notification.system.model.entities.NotificationChannel;
import com.gtzuc.projects.notification.system.model.entities.Topic;
import com.gtzuc.projects.notification.system.repositories.MessageRepository;
import com.gtzuc.projects.notification.system.repositories.NotificationChannelRepository;
import com.gtzuc.projects.notification.system.repositories.TopicRepository;
import com.gtzuc.projects.notification.system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private NotificationChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository; // Assuming you have this

    /**
     * Creates a message with validation.
     * Checks if topic exists, if not creates a new one.
     * Validates if channel exists.
     * Validates if user exists.
     */
    @Transactional
    public void createNotification(MessageRequestDTO requestDTO) {

        // 1. Validate if user exists
        if (!userRepository.existsById(requestDTO.getUserId())) {
            throw new RuntimeException("User not found with id: " + requestDTO.getUserId());
        }

        // 2. Handle Topic: Find existing or create new
        Topic topic = getOrCreateTopic(requestDTO.getTopicName());

        // 3. Get or validate Notification Channel
        NotificationChannel channel = channelRepository.findByName(requestDTO.getChannelName())
                .orElseThrow(() -> new RuntimeException("Channel not found: " + requestDTO.getChannelName()));

        // 4. Create and save the message
        Message message = new Message();
        message.setUserId(requestDTO.getUserId());
        message.setTopic(topic);
        message.setChannel(channel);
        message.setMessage(requestDTO.getMessageContent());
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
    }

    /**
     * Helper method to get existing topic or create new one
     */
    @Transactional
    public Topic getOrCreateTopic(String topicName) {
        // Try to find existing topic
        return topicRepository.findByName(topicName)
                .orElseGet(() -> {
                    // Create new topic if doesn't exist
                    Topic newTopic = new Topic();
                    newTopic.setName(topicName);
                    return topicRepository.save(newTopic);
                });
    }

    public List<Message> getMessagesByUserId(Long userId) {

        return messageRepository.findByUserId(userId);
    }
}
