package com.ivanfrias.myturn.notifications.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ivanfrias.myturn.model.NotificationDto;
import com.ivanfrias.myturn.notifications.dao.models.entities.NotificationEntity;
import com.ivanfrias.myturn.notifications.dao.models.enums.NotificationTypeEnum;
import com.ivanfrias.myturn.notifications.dao.repositories.NotificationRepository;
import com.ivanfrias.myturn.notifications.properties.AppProps;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @InjectMocks private NotificationService notificationService;

  @Mock private NotificationRepository notificationRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private MailService mailService;

  private NotificationEntity notificationEntity;

  @BeforeEach
  void setUp() {
    notificationEntity = new NotificationEntity();
    notificationEntity.setId(1L);
    notificationEntity.setEmailTo("test@test.com");
    notificationEntity.setSubject("Test Subject");
    notificationEntity.setText("Test Text");
    notificationEntity.setIsSended(false);
  }

  @Nested
  @DisplayName("SaveNotification Tests")
  class SaveNotificationTests {
    @Test
    @DisplayName("Should map DTO and save entity")
    void saveNotification_shouldMapAndSaveChanges() {
      // Arrange
      NotificationDto dto = new NotificationDto();
      when(modelMapper.map(dto, NotificationEntity.class)).thenReturn(notificationEntity);
      when(notificationRepository.save(notificationEntity)).thenReturn(notificationEntity);

      // Act
      NotificationEntity result = notificationService.saveNotification(dto);

      // Assert
      verify(modelMapper).map(dto, NotificationEntity.class);
      verify(notificationRepository).save(notificationEntity);
      assertNotNull(result);
      assertEquals(1L, result.getId());
    }
  }

  @Nested
  @DisplayName("GetNotificationsNotSendedByType Tests")
  class GetNotificationsNotSendedByTypeTests {
    @Test
    @DisplayName("Should call repository and return list")
    void getNotificationsNotSendedByType_shouldCallRepository() {
      // Arrange
      when(notificationRepository.findByNotificationTypeAndIsSendedFalse(
              NotificationTypeEnum.END_SUBSCRIPTION))
          .thenReturn(Collections.singletonList(notificationEntity));

      // Act
      List<NotificationEntity> result =
          notificationService.getNotificationsNotSendedByType(
              NotificationTypeEnum.END_SUBSCRIPTION);

      // Assert
      verify(notificationRepository)
          .findByNotificationTypeAndIsSendedFalse(NotificationTypeEnum.END_SUBSCRIPTION);
      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertEquals(1, result.size());
    }
  }

  @Nested
  @DisplayName("SendNotifications Tests")
  class SendNotificationsTests {
    @Test
    @DisplayName("Should send mail and update status when email is not mocked")
    void sendNotifications_whenEmailIsNotMocked_shouldSendMailAndUpdateStatus() {
      // Arrange
      AppProps props = new AppProps(new AppProps.Mail(false, "Fake Sender", "fake@email.com"));
      ReflectionTestUtils.setField(notificationService, "appProps", props);

      doNothing().when(mailService).sendPlainText(anyString(), anyString(), anyString());
      List<NotificationEntity> notifications = Collections.singletonList(notificationEntity);

      // Act
      notificationService.sendNotifications(notifications);

      // Assert
      verify(mailService).sendPlainText("test@test.com", "Test Subject", "Test Text");
      assertTrue(notificationEntity.getIsSended());
    }

    @Test
    @DisplayName("Should not send mail but update status when email is mocked")
    void sendNotifications_whenEmailIsMocked_shouldLogWarningAndUpdateStatus() {
      // Arrange
      AppProps props = new AppProps(new AppProps.Mail(true, "Fake Sender", "fake@email.com"));
      ReflectionTestUtils.setField(notificationService, "appProps", props);

      List<NotificationEntity> notifications = Collections.singletonList(notificationEntity);

      // Act
      notificationService.sendNotifications(notifications);

      // Assert
      verify(mailService, never()).sendPlainText(anyString(), anyString(), anyString());
      assertTrue(notificationEntity.getIsSended());
    }

    @Test
    @DisplayName("Should do nothing for an empty list")
    void sendNotifications_withEmptyList_shouldDoNothing() {
      // Arrange
      List<NotificationEntity> notifications = Collections.emptyList();

      // Act
      notificationService.sendNotifications(notifications);

      // Assert
      verifyNoInteractions(mailService);
    }
  }
}
