package com.ivanfrias.myturn.notifications.dao.repositories;

import com.ivanfrias.myturn.notifications.dao.models.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

}

