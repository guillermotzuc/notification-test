package com.gtzuc.projects.notification.system.service;

import com.gtzuc.projects.notification.system.model.Channels;
import com.gtzuc.projects.notification.system.model.dto.MessageRequestDTO;
import com.gtzuc.projects.notification.system.model.entities.*;
import com.gtzuc.projects.notification.system.repositories.*;
import com.gtzuc.projects.notification.system.service.channels.Notification;
import com.gtzuc.projects.notification.system.service.channels.NotificationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private NotificationChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationFactory factory;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private NotificationService notificationService;

    private MessageRequestDTO validRequestDTO;
    private User validUser;
    private Topic existingTopic;
    private NotificationChannel emailChannel;
    private NotificationChannel smsChannel;
    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        // Setup valid request DTO
        validRequestDTO = new MessageRequestDTO();
        validRequestDTO.setUserId(1L);
        validRequestDTO.setTopicName("Sports");
        validRequestDTO.setMessageContent("Game starts at 7 PM");

        // Setup valid user with multiple channels
        validUser = new User();
        validUser.setId(1L);
        validUser.setName("John Doe");
        validUser.setEmail("john@example.com");
        validUser.setChannels("EMAIL,SMS");
        validUser.setPhoneNumber("+1234567890");
        validUser.setTopics("Movies,Sports,Technology");

        // Setup existing topic
        existingTopic = new Topic();
        existingTopic.setId(1L);
        existingTopic.setName("Sports");

        // Setup channels
        emailChannel = new NotificationChannel();
        emailChannel.setId(1L);
        emailChannel.setName(Channels.EMAIL);

        smsChannel = new NotificationChannel();
        smsChannel.setId(2L);
        smsChannel.setName(Channels.SMS);

        // Setup mock notification
        mockNotification = mock(Notification.class);
    }

    @Test
    @DisplayName("Should successfully create notification for user with multiple channels")
    void createNotification_Success_WithMultipleChannels() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(topicRepository.findByName("Sports")).thenReturn(Optional.of(existingTopic));
        when(channelRepository.findByName(Channels.EMAIL)).thenReturn(Optional.of(emailChannel));
        when(channelRepository.findByName(Channels.SMS)).thenReturn(Optional.of(smsChannel));
        when(factory.getChannel(eq(validUser), eq(Channels.EMAIL))).thenReturn(mockNotification);
        when(factory.getChannel(eq(validUser), eq(Channels.SMS))).thenReturn(mockNotification);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificationService.createNotification(validRequestDTO);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).findByName("Sports");
        verify(channelRepository, times(2)).findByName(any(Channels.class));
        verify(factory, times(2)).getChannel(eq(validUser), any(Channels.class));
        verify(mockNotification, times(2)).sendNotification(eq(validUser), any(Message.class));
        verify(messageRepository, times(2)).save(any(Message.class));
    }

    @Test
    @DisplayName("Should create new topic when it doesn't exist")
    void createNotification_CreatesNewTopic_WhenTopicNotFound() {
        // Given
        Topic newTopic = new Topic();
        newTopic.setName("Technology");

        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(topicRepository.findByName("Technology")).thenReturn(Optional.empty());
        when(topicRepository.save(any(Topic.class))).thenReturn(newTopic);
        when(channelRepository.findByName(any())).thenReturn(Optional.ofNullable(emailChannel));
        when(factory.getChannel(eq(validUser), eq(Channels.EMAIL))).thenReturn(mockNotification);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Update request with new topic
        validRequestDTO.setTopicName("Technology");

        // When
        notificationService.createNotification(validRequestDTO);

        // Then
        verify(topicRepository, times(1)).findByName("Technology");
        verify(topicRepository, times(1)).save(any(Topic.class));
        ArgumentCaptor<Topic> topicCaptor = ArgumentCaptor.forClass(Topic.class);
        verify(topicRepository).save(topicCaptor.capture());
        assertThat(topicCaptor.getValue().getName()).isEqualTo("Technology");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void createNotification_ThrowsException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        validRequestDTO.setUserId(999L);

        // When & Then
        assertThatThrownBy(() -> notificationService.createNotification(validRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository, times(1)).findById(999L);
        verify(topicRepository, never()).findByName(anyString());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should throw exception when user has no channels subscribed")
    void createNotification_ThrowsException_WhenUserHasNoChannels() {
        // Given
        User userWithNoChannels = new User();
        userWithNoChannels.setId(1L);
        userWithNoChannels.setChannels(null); // or empty string

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithNoChannels));

        // When & Then
        assertThatThrownBy(() -> notificationService.createNotification(validRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The user is not subscribe to any notification channel");

        verify(userRepository, times(1)).findById(1L);
        verify(topicRepository, never()).findByName(anyString());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should throw exception when channel not found")
    void createNotification_ThrowsException_WhenChannelNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(topicRepository.findByName("Sports")).thenReturn(Optional.of(existingTopic));
        when(channelRepository.findByName(Channels.EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.createNotification(validRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Channel not found: EMAIL");

        verify(channelRepository, times(1)).findByName(Channels.EMAIL);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should save message with correct data")
    void createNotification_SavesMessageWithCorrectData() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(topicRepository.findByName("Sports")).thenReturn(Optional.of(existingTopic));
        when(channelRepository.findByName(any())).thenReturn(Optional.of(emailChannel));
        when(factory.getChannel(eq(validUser), eq(Channels.EMAIL))).thenReturn(mockNotification);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        when(messageRepository.save(messageCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificationService.createNotification(validRequestDTO);

        // Save 2 times as user has two valid channels
        verify(messageRepository, times(2)).save(any(Message.class));
        Message savedMessage = messageCaptor.getValue();

        assertThat(savedMessage.getUserId()).isEqualTo(1L);
        assertThat(savedMessage.getTopic()).isEqualTo(existingTopic);
        assertThat(savedMessage.getChannel()).isEqualTo(emailChannel);
        assertThat(savedMessage.getMessage()).isEqualTo("Game starts at 7 PM");
        assertThat(savedMessage.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle single channel user correctly")
    void createNotification_WithSingleChannel_Success() {
        // Given
        User singleChannelUser = new User();
        singleChannelUser.setId(2L);
        singleChannelUser.setChannels("EMAIL");
        singleChannelUser.setTopics("Movies,Sports");

        when(userRepository.findById(2L)).thenReturn(Optional.of(singleChannelUser));
        when(topicRepository.findByName("Sports")).thenReturn(Optional.of(existingTopic));
        when(channelRepository.findByName(Channels.EMAIL)).thenReturn(Optional.of(emailChannel));
        when(factory.getChannel(eq(singleChannelUser), eq(Channels.EMAIL))).thenReturn(mockNotification);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        validRequestDTO.setUserId(2L);

        // When
        notificationService.createNotification(validRequestDTO);

        // Then
        verify(channelRepository, times(1)).findByName(any(Channels.class));
        verify(factory, times(1)).getChannel(eq(singleChannelUser), any(Channels.class));
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("Should rollback transaction when exception occurs")
    void createNotification_RollsBack_WhenExceptionOccurs() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(topicRepository.findByName("Sports")).thenReturn(Optional.of(existingTopic));
        when(channelRepository.findByName(Channels.EMAIL)).thenReturn(Optional.of(emailChannel));
        when(factory.getChannel(eq(validUser), eq(Channels.EMAIL))).thenThrow(new RuntimeException("Notification failed"));

        // When & Then
        assertThatThrownBy(() -> notificationService.createNotification(validRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notification failed");

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should return existing topic when found")
    void getOrCreateTopic_ReturnsExistingTopic() {
        // Given
        when(topicRepository.findByName("Sports")).thenReturn(Optional.of(existingTopic));

        // When
        Topic result = notificationService.getOrCreateTopic("Sports");

        // Then
        assertThat(result).isEqualTo(existingTopic);
        verify(topicRepository, times(1)).findByName("Sports");
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    @DisplayName("Should create and return new topic when not found")
    void getOrCreateTopic_CreatesNewTopic_WhenNotFound() {
        // Given
        String newTopicName = "New Topic";
        Topic newTopic = new Topic();
        newTopic.setName(newTopicName);

        when(topicRepository.findByName(newTopicName)).thenReturn(Optional.empty());
        when(topicRepository.save(any(Topic.class))).thenReturn(newTopic);

        // When
        Topic result = notificationService.getOrCreateTopic(newTopicName);

        // Then
        assertThat(result).isEqualTo(newTopic);
        assertThat(result.getName()).isEqualTo(newTopicName);
        verify(topicRepository, times(1)).findByName(newTopicName);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    @DisplayName("Should return list of notification logs for user")
    void getMessagesByUserId_ReturnsLogs_WhenUserHasMessages() {
        // Given
        Long userId = 1L;
        List<NotificationLog> expectedLogs = Arrays.asList(
                createNotificationLog(1L, userId, "Message 1"),
                createNotificationLog(2L, userId, "Message 2")
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));
        when(notificationLogRepository.findByUserIdAndTopics(anyLong(), anyList())).thenReturn(expectedLogs);

        // When
        List<NotificationLog> result = notificationService.getMessagesByUserId(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedLogs);
        verify(notificationLogRepository, times(1)).findByUserIdAndTopics(anyLong(), anyList());
    }

    @Test
    @DisplayName("Should return empty list when user has no messages")
    void getMessagesByUserId_ReturnsEmptyList_WhenUserHasNoMessages() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));
        when(notificationLogRepository.findByUserIdAndTopics(anyLong(), anyList())).thenReturn(Arrays.asList());

        // When
        List<NotificationLog> result = notificationService.getMessagesByUserId(userId);

        // Then
        assertThat(result).isEmpty();
        verify(notificationLogRepository, times(1)).findByUserIdAndTopics(anyLong(), anyList());
    }


    private NotificationLog createNotificationLog(Long id, Long userId, String message) {
        NotificationLog log = new NotificationLog();
        log.setUserId(id);
        log.setUserId(userId);
        log.setMessage(message);
        log.setMessageTimestamp(LocalDateTime.now());
        return log;
    }
}