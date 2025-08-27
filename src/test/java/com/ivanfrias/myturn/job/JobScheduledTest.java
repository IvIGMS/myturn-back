package com.ivanfrias.myturn.job;

import com.ivanfrias.myturn.companies.services.CompanyService;
import com.ivanfrias.myturn.model.CompanyDTO;
import com.ivanfrias.myturn.model.NotificationDto;
import com.ivanfrias.myturn.model.UserDTO;
import com.ivanfrias.myturn.notifications.dao.models.entities.NotificationEntity;
import com.ivanfrias.myturn.notifications.dao.models.enums.NotificationTypeEnum;
import com.ivanfrias.myturn.notifications.services.NotificationService;
import com.ivanfrias.myturn.subscriptions.services.SubscriptionService;
import com.ivanfrias.myturn.users.dao.dto.UserDownDto;
import com.ivanfrias.myturn.users.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobScheduledTest {

    @InjectMocks
    private JobScheduled jobScheduled;

    @Mock
    private UserService userService;
    @Mock
    private CompanyService companyService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private SubscriptionService subscriptionService;

    @Nested
    @DisplayName("CreateNotifications Tests")
    class CreateNotificationsTests {

        @Test
        @DisplayName("Should create notifications and disable subscriptions when users are down")
        void createNotifications_whenUsersAreDown_shouldProcessThem() {
            // Arrange
            UserDownDto user1 = mockUserDownDto(1L, 10L, "John", "Doe");
            UserDownDto user2 = mockUserDownDto(2L, 10L, "Jane", "Roe");
            List<UserDownDto> usersDown = List.of(user1, user2);

            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(10L);
            companyDTO.setOwnerId(100L);

            UserDTO ownerDTO = new UserDTO();
            ownerDTO.setId(100L);
            ownerDTO.setFirstname("Peter");
            ownerDTO.setLastname("Jones");
            ownerDTO.setEmail("peter.jones@test.com");

            when(userService.usersDownToday()).thenReturn(usersDown);
            when(companyService.getCompanyById(10L)).thenReturn(companyDTO);
            when(userService.getUserDTOById(100L)).thenReturn(ownerDTO);

            // Act
            jobScheduled.createNotifications();

            // Assert
            verify(userService).usersDownToday();
            verify(companyService).getCompanyById(10L);
            verify(userService).getUserDTOById(100L);

            ArgumentCaptor<NotificationDto> notificationCaptor = ArgumentCaptor.forClass(NotificationDto.class);
            verify(notificationService).saveNotification(notificationCaptor.capture());
            NotificationDto capturedDto = notificationCaptor.getValue();
            assertEquals("peter.jones@test.com", capturedDto.getEmailTo());
            assertEquals("Fin de suscripción", capturedDto.getSubject());
            assertTrue(capturedDto.getText().contains("John Doe"));
            assertTrue(capturedDto.getText().contains("Jane Roe"));

            verify(subscriptionService).disableSubscription(10L, 1L);
            verify(subscriptionService).disableSubscription(10L, 2L);
        }

        @Test
        @DisplayName("Should do nothing when no users are down")
        void createNotifications_whenNoUsersAreDown_shouldDoNothing() {
            // Arrange
            when(userService.usersDownToday()).thenReturn(Collections.emptyList());

            // Act
            jobScheduled.createNotifications();

            // Assert
            verify(userService).usersDownToday();
            verifyNoInteractions(companyService, notificationService, subscriptionService);
        }
    }

    @Nested
    @DisplayName("SendNotifications Tests")
    class SendNotificationsTests {

        @Test
        @DisplayName("Should send notifications when unsent notifications exist")
        void sendNotifications_whenUnsentNotificationsExist_shouldSendThem() {
            // Arrange
            List<NotificationEntity> notifications = List.of(new NotificationEntity(), new NotificationEntity());
            when(notificationService.getNotificationsNotSendedByType(NotificationTypeEnum.END_SUBSCRIPTION)).thenReturn(notifications);

            // Act
            jobScheduled.sendNotifications();

            // Assert
            verify(notificationService).getNotificationsNotSendedByType(NotificationTypeEnum.END_SUBSCRIPTION);
            verify(notificationService).sendNotifications(notifications);
        }

        @Test
        @DisplayName("Should do nothing when no unsent notifications exist")
        void sendNotifications_whenNoUnsentNotificationsExist_shouldDoNothing() {
            // Arrange
            NotificationEntity notification = NotificationEntity.builder().build();

            when(notificationService.getNotificationsNotSendedByType(NotificationTypeEnum.END_SUBSCRIPTION)).thenReturn(List.of(notification));
            doNothing().when(notificationService).sendNotifications(any());
            // Act
            jobScheduled.sendNotifications();

            // Assert
            verify(notificationService).getNotificationsNotSendedByType(NotificationTypeEnum.END_SUBSCRIPTION);
            verify(notificationService).sendNotifications(any());
        }
    }

    private UserDownDto mockUserDownDto(Long userId, Long companyId, String name, String lastName) {
        return new UserDownDto() {
            @Override
            public Long getUserId() { return userId; }
            @Override
            public Long getCompanyId() { return companyId; }
            @Override
            public String getName() { return name; }
            @Override
            public String getLastName() { return lastName; }
        };
    }
}
