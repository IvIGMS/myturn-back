package com.ivanfrias.myturn.subscriptions.services;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.security.services.UserService;
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
    private final UserService userService;

    @Transactional
    public SubscriptionDTO createSubscription(Long userId, LocalDate startDate, int durationInMonths) {
        UserEntity user = userService.getUserEntityById(userId);

        LocalDate endDate = startDate.plusMonths(durationInMonths);

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        return modelMapper.map(savedSubscription, SubscriptionDTO.class);
    }

    public List<SubscriptionDTO> getSubscriptionsByUserId(Long userId) {
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findByUserIdOrderByStartDateDesc(userId);
        return subscriptions.stream()
                .map(subscription -> modelMapper.map(subscription, SubscriptionDTO.class))
                .toList();
    }

    public SubscriptionDTO getSubscriptionById(Long subscriptionId) {
        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Suscripción no encontrada con ID: " + subscriptionId));
        return modelMapper.map(subscription, SubscriptionDTO.class);
    }

    public List<SubscriptionDTO> getAllSubscriptions() {
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findAll();
        return subscriptions.stream()
                .map(subscription -> modelMapper.map(subscription, SubscriptionDTO.class))
                .toList();
    }
}
