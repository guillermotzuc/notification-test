package com.gtzuc.projects.notification.system.service.channels;

import com.gtzuc.projects.notification.system.model.entities.Message;
import com.gtzuc.projects.notification.system.model.entities.User;

public interface Notification {

    /**
     * Sends a notification to the specified user using the provided message data.
     *
     * <p>This method must be implemented by subclasses to define the specific
     * notification delivery mechanism (e.g., email, SMS, push notification).</p>
     *
     * @param user the recipient of the notification; must not be {@code null}
     * @param message the message content and metadata to send; must not be {@code null}
     */
    abstract void sendNotification(User user, Message message);
}
