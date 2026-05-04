package com.gtzuc.projects.notification.system.service;

import com.gtzuc.projects.notification.system.model.Channels;
import com.gtzuc.projects.notification.system.model.dto.MessageRequestDTO;
import com.gtzuc.projects.notification.system.model.entities.*;
import com.gtzuc.projects.notification.system.repositories.*;
import com.gtzuc.projects.notification.system.service.channels.Notification;
import com.gtzuc.projects.notification.system.service.channels.NotificationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private NotificationChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationFactory factory;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    /**
     * Creates a message with validation.
     * Checks if topic exists, if not creates a new one.
     * Validates if channel exists.
     * Validates if user exists.
     */
    @Transactional
    public void createNotification(MessageRequestDTO requestDTO) {

        // Validate if user exists
        Optional<User> user = userRepository.findById(requestDTO.getUserId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with id: " + requestDTO.getUserId());
        }

        if (user.get().getChannels() == null || user.get().getChannels().isEmpty()) {
            throw new RuntimeException("The user is not subscribe to any notification channel");
        }

        // Handle Topic: Find existing or create new
        Topic topic = getOrCreateTopic(requestDTO.getTopicName());

        // Create a message for each channel
        String[] channels = user.get().getChannels().split(",");
        for (String channelName : channels) {

            // Get or validate Notification Channel
            NotificationChannel channel = channelRepository.findByName(Channels.valueOf(channelName))
                    .orElseThrow(() -> new RuntimeException("Channel not found: " + channelName));

            // Create the message
            Message message = new Message();
            message.setUserId(requestDTO.getUserId());
            message.setTopic(topic);
            message.setChannel(channel);
            message.setMessage(requestDTO.getMessageContent());
            message.setTimestamp(LocalDateTime.now());

            // Send the notification
            Notification notification = factory.getChannel(user.get(), channel.getName());
            notification.sendNotification(user.get(), message);

            messageRepository.save(message);
        }
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

    /**
     * Return messages by user
     * @param userId
     * @return
     */
    public List<NotificationLog> getMessagesByUserId(Long userId) {

        return notificationLogRepository.findByUserId(userId);
    }
}
