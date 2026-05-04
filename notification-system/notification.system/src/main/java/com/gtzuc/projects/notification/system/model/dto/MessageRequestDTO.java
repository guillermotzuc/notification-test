package com.gtzuc.projects.notification.system.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class MessageRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Topic name is required")
    private String topicName;

    @NotBlank(message = "Channel name is required")
    private String channelName;

    @NotBlank(message = "Message content is required")
    private String messageContent;
}