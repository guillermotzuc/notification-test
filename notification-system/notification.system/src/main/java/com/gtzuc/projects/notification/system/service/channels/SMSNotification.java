package com.gtzuc.projects.notification.system.service.channels;

import com.gtzuc.projects.notification.system.model.Channels;
import com.gtzuc.projects.notification.system.model.entities.Message;
import com.gtzuc.projects.notification.system.model.entities.User;
import org.springframework.stereotype.Service;

@Service("SMS")
public class SMSNotification implements Notification {
    @Override
    public void sendNotification(User user, Message message) {
        message.setMessage(String.format("[%s] %s", Channels.SMS.name(), message.getMessage()));
    }
}
