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

    @Query("SELECT n FROM NotificationLog n WHERE n.userId = :userId AND n.topicName IN (:topics)")
    List<NotificationLog> findByUserIdAndTopics(@Param("userId") Long userId, @Param("topics") List<String> topics);
}
