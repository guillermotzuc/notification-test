package com.gtzuc.projects.notification.system.service.channels;

import com.gtzuc.projects.notification.system.model.Channels;
import com.gtzuc.projects.notification.system.model.entities.User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationFactory {

    private final Map<String, Notification> channels;

    public NotificationFactory(Map<String, Notification> channels) {
        this.channels = channels;
    }

    public Notification getChannel(User user, Channels channel) {
        Notification notification = channels.get(channel.name());

        if (notification == null || !user.getChannels().contains(channel.toString())) {
            throw new IllegalArgumentException(
                    "Unsupported notification channel: " + channel
            );
        }

        return notification;
    }
}
