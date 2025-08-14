package com.ivanfrias.myturn.subscriptions.services;

import com.ivanfrias.myturn.common.exceptions.ConflictException;
import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.companies.services.CompanyService;
import com.ivanfrias.myturn.security.dao.models.enums.RoleEnum;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CompanyService companyService;

    @Transactional
    public SubscriptionDTO createSubscription(Long userId, LocalDate startDate, int durationInMonths, Long ownerId) {
        if(0==durationInMonths) {
            throw new ConflictException("La duración de la suscripción no puede ser 0");
        }
        // Si es nueva suscripcion o ha expirado
        UserEntity user = userService.getUserEntityById(userId);

        isLinkedOwnerAndUser(userId, ownerId);

        LocalDate endDate = startDate.plusMonths(durationInMonths);

        LocalDate availableDate = isAvailableYet(userId);
        // Si ya hay una activa
        if(Objects.nonNull(availableDate)) {
            startDate = availableDate.plusDays(1);
            endDate = startDate.plusMonths(durationInMonths);
        }

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .user(user)
                .company(user.getCompany())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        return modelMapper.map(savedSubscription, SubscriptionDTO.class);
    }

    private LocalDate isAvailableYet(Long userId) {
        LocalDate currentDate = LocalDate.now();
        return subscriptionRepository.isAvailableYet(userId, currentDate);
    }

    public List<SubscriptionDTO> getSubscriptionsByUserId(Long userId, Long ownerId) {
        UserEntity user = userService.getUserEntityById(userId);
        if(!user.getRole().equals(RoleEnum.USER)) {
            throw new ConflictException("No se pueden ver las suscripciones de un user que no sea role USER");
        }
        isLinkedOwnerAndUser(userId, ownerId);
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findByUserIdOrderByStartDateDesc(user.getId());
        return subscriptions.stream()
                .map(subscription -> modelMapper.map(subscription, SubscriptionDTO.class))
                .toList();
    }

    public SubscriptionDTO getSubscriptionById(Long subscriptionId) {
        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Suscripción no encontrada con ID: " + subscriptionId));
        return modelMapper.map(subscription, SubscriptionDTO.class);
    }

    private void isLinkedOwnerAndUser(Long userId, Long ownerId) {
        UserEntity user = userService.getUserEntityById(userId);
        CompanyEntity ownerCompany = companyService.findCompanyEntityByOwnerId(ownerId);

        if(Objects.isNull(user.getCompany())) {
            throw new ConflictException("El usuario para el que se va a crear una suscripción aún no tiene una empresa asignada");
        }

        if (!user.getCompany().getId().equals(ownerCompany.getId())) {
            throw new ConflictException("El usuario no pertenece a la misma empresa que el administrador, no tiene permisos para crear suscripciones");
        }
    }
}
