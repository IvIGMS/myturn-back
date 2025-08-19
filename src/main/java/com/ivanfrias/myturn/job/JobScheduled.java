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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobScheduled {
    private static final Logger log = LoggerFactory.getLogger(JobScheduled.class);

    private final UserService userService;
    private final CompanyService companyService;
    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;

    //@Scheduled(cron = "0 0 1 * * *", zone = "Europe/Madrid")
    @Transactional
    @Scheduled(fixedRate = 10000_000, initialDelay = 5_000)
    public void createNotifications() {
        List<UserDownDto> userDownToday = userService.usersDownToday();

        Map<Long, List<UserDownDto>> userByCompany =
                userDownToday.stream()
                        .collect(Collectors.groupingBy(UserDownDto::getCompanyId));

        userByCompany.forEach((key, values) -> {
            CompanyDTO companyDTO = companyService.getCompanyById(key);
            UserDTO owner = userService.getUserDTOById(companyDTO.getOwnerId());

            NotificationDto notificationDto = createNotificationStructure(owner, values);
            notificationService.saveNotification(notificationDto);

            values.forEach(user ->
                    subscriptionService.disableSubscription(companyDTO.getId(), user.getUserId())
            );
        });
    }

    private NotificationDto createNotificationStructure(UserDTO owner, List<UserDownDto> values) {
        StringBuilder message = new StringBuilder();
        message.append("Hola " + owner.getFirstname() + " " + owner.getLastname() + "\n")
                .append("Ha finalizado la suscripción de los siguientes usuarios:\n");

        values.forEach(user ->
                message.append(user.getName() + " " + user.getLastName() + "\n")
        );

        return NotificationDto.builder()
                .emailTo(owner.getEmail())
                .subject("Fin de suscripción")
                .text(message.toString())
                .notificationType(NotificationTypeEnum.END_SUBSCRIPTION.getValue())
                .isSended(false)
                .build();
    }

    //@Scheduled(cron = "0 0 9 * * *", zone = "Europe/Madrid")
    @Transactional
    @Scheduled(fixedRate = 10000_000, initialDelay = 30_000)
    public void sendNotifications() {
        List<NotificationEntity> notifications = notificationService.getNotificationsNotSendedByType(NotificationTypeEnum.END_SUBSCRIPTION);
        notificationService.sendNotifications(notifications);
    }
}
