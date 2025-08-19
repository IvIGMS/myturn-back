package com.ivanfrias.myturn.notifications.dao.repositories;

import com.ivanfrias.myturn.notifications.dao.models.entities.NotificationEntity;
import com.ivanfrias.myturn.notifications.dao.models.enums.NotificationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByNotificationTypeAndIsSendedFalse(NotificationTypeEnum notificationType);
}

