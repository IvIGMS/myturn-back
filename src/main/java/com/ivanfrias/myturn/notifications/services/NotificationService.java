package com.ivanfrias.myturn.notifications.services;

import com.ivanfrias.myturn.model.NotificationDto;
import com.ivanfrias.myturn.notifications.dao.models.entities.NotificationEntity;
import com.ivanfrias.myturn.notifications.dao.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public NotificationEntity saveNotification(NotificationDto notificationDto) {
        NotificationEntity notificationEntityToSave = modelMapper.map(notificationDto, NotificationEntity.class);
        NotificationEntity notificationEntitySaved = notificationRepository.save(notificationEntityToSave);
        // todo: enviar al servicio de sendEmail
        return notificationEntitySaved;
    }
}
