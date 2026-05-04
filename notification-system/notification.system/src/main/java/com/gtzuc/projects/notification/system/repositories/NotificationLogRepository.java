package com.gtzuc.projects.notification.system.repositories;

import com.gtzuc.projects.notification.system.model.entities.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByUserId(Long userId);

    @Query("SELECT u FROM NotificationLog u WHERE u.userId = :userId AND u.topicName = :topicName AND u.channelName = :channelName")
    List<NotificationLog> findByUserTopicAndChannel(@Param("userId") Long userId,
                                                           @Param("topicName") String topicName,
                                                           @Param("channelName") String channelName);

    @Query("SELECT u FROM NotificationLog u WHERE u.userId = :userId AND u.topicName = :topicName")
    List<NotificationLog> findByUserTopic(@Param("userId") Long userId,
                                                    @Param("topicName") String topicName);
}
