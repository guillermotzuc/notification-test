package com.gtzuc.projects.notification.system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Topic name is required")
    private String topicName;

    @NotBlank(message = "Message content is required")
    private String messageContent;
}