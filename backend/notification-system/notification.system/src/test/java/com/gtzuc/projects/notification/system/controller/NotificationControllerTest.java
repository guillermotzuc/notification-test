package com.gtzuc.projects.notification.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtzuc.projects.notification.system.model.dto.MessageRequestDTO;
import com.gtzuc.projects.notification.system.model.entities.NotificationLog;
import com.gtzuc.projects.notification.system.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private ObjectMapper objectMapper;
    private MessageRequestDTO sampleRequest;
    private NotificationLog sampleLog;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper();

        // Initialize sample data (adjust fields based on your DTOs)
        sampleRequest = new MessageRequestDTO();
        sampleRequest.setUserId(1L);
        sampleRequest.setMessageContent("Test message");
        sampleRequest.setTopicName("Movies");

        sampleLog = new NotificationLog();
        sampleLog.setUserId(1L);
        sampleLog.setUserId(1L);
        sampleLog.setMessage("Test message");
        sampleLog.setMessageTimestamp(LocalDateTime.now());
    }

    @Test
    @DisplayName("Create notification")
    void createNotification_Success_ReturnsCreated() throws Exception {
        // Given
        doNothing().when(notificationService).createNotification(any(MessageRequestDTO.class));

        // When & Then
        mockMvc.perform(post("/api/v1/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("The notification was sent"));

        verify(notificationService, times(1)).createNotification(any(MessageRequestDTO.class));
    }

    @Test
    @DisplayName("Create notification with bad request result")
    void createNotification_ValidationFails_ReturnsBadRequest() throws Exception {
        MessageRequestDTO invalidRequest = new MessageRequestDTO();

        // When & Then
        mockMvc.perform(post("/api/v1/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(notificationService, never()).createNotification(any());
    }

    @Test
    @DisplayName("Create notification with runtime exception")
    void createNotification_ServiceThrowsRuntimeException_ReturnsBadRequest() throws Exception {
        // Given
        String errorMessage = "User not found";
        doThrow(new RuntimeException(errorMessage))
                .when(notificationService).createNotification(any(MessageRequestDTO.class));

        // When & Then
        mockMvc.perform(post("/api/v1/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(notificationService, times(1)).createNotification(any(MessageRequestDTO.class));
    }

    @Test
    @DisplayName("Create notification with invalid user")
    void getNotificationsByUser_WithValidUserId_ReturnsMessages() throws Exception {
        // Given
        Long userId = 1L;
        List<NotificationLog> logs = Arrays.asList(sampleLog);
        when(notificationService.getMessagesByUserId(userId)).thenReturn(logs);

        // When & Then
        mockMvc.perform(get("/api/v1/notification/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(notificationService, times(1)).getMessagesByUserId(userId);
    }

    @Test
    @DisplayName("Get notification empty list")
    void getNotificationsByUser_WithNoMessages_ReturnsEmptyList() throws Exception {
        // Given
        Long userId = 99L;
        when(notificationService.getMessagesByUserId(userId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/notification/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(notificationService, times(1)).getMessagesByUserId(userId);
    }

    @Test
    @DisplayName("Get notification invalid user id")
    void getNotificationsByUser_WithInvalidUserId_ReturnsEmptyList() throws Exception {
        // Given
        Long userId = -1L;
        when(notificationService.getMessagesByUserId(userId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/notification/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(notificationService, times(1)).getMessagesByUserId(userId);
    }
}
