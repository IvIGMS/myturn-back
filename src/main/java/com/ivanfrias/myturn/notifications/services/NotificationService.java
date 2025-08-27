package com.ivanfrias.myturn.notifications.services;

import com.ivanfrias.myturn.model.NotificationDto;
import com.ivanfrias.myturn.notifications.dao.models.entities.NotificationEntity;
import com.ivanfrias.myturn.notifications.dao.models.enums.NotificationTypeEnum;
import com.ivanfrias.myturn.notifications.dao.repositories.NotificationRepository;
import com.ivanfrias.myturn.notifications.properties.AppProps;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final ModelMapper modelMapper;
  private final MailService mailService;
  private final AppProps appProps;

  public NotificationEntity saveNotification(NotificationDto notificationDto) {
    NotificationEntity notificationEntityToSave =
        modelMapper.map(notificationDto, NotificationEntity.class);
    NotificationEntity notificationEntitySaved =
        notificationRepository.save(notificationEntityToSave);
    // todo: enviar al servicio de sendEmail
    return notificationEntitySaved;
  }

  public List<NotificationEntity> getNotificationsNotSendedByType(
      NotificationTypeEnum notificationTypeEnum) {
    return notificationRepository.findByNotificationTypeAndIsSendedFalse(notificationTypeEnum);
  }

  public void sendNotifications(List<NotificationEntity> notifications) {
    notifications.forEach(
        n -> {
          if (!appProps.mail().isEmailMocked()) {
            mailService.sendPlainText(n.getEmailTo(), n.getSubject(), n.getText());
            log.info("Email enviado correctamente a {}", n.getEmailTo());
            n.setIsSended(true);
          } else {
            log.warn(
                "[EMAIL MOCKED] No se envía email real. to={}, subject={}",
                n.getEmailTo(),
                n.getSubject());
            n.setIsSended(
                true); // Le ponemos isSended aunque no se envíe para que la app actue igual
          }
        });
  }
}
