package com.ivanfrias.myturn.subscriptions.services;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.subscriptions.dao.models.entities.SubscriptionEntity;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.subscriptions.dao.repositories.SubscriptionRepository;
import com.ivanfrias.myturn.security.dao.repositories.UserRepository;
import com.ivanfrias.myturn.model.SubscriptionDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public SubscriptionDTO createSubscription(Long userId, LocalDate startDate, int durationInMonths) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId));

        LocalDate endDate = startDate.plusMonths(durationInMonths);

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .user(user)
                .build();

        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        return modelMapper.map(savedSubscription, SubscriptionDTO.class);
    }

    public List<SubscriptionEntity> getSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findByUserIdOrderByStartDateDesc(userId);
    }

    public SubscriptionEntity getSubscriptionById(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Suscripción no encontrada con ID: " + subscriptionId));
    }
}
