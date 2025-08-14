package com.ivanfrias.myturn.subscriptions.controllers;

import com.ivanfrias.myturn.api.SubscriptionsApi;
import com.ivanfrias.myturn.common.exceptions.utils.ControllerUtils;
import com.ivanfrias.myturn.common.exceptions.utils.UnauthorizedException;
import com.ivanfrias.myturn.model.CreateSubscriptionRequestDTO;
import com.ivanfrias.myturn.model.SubscriptionDTO;
import com.ivanfrias.myturn.subscriptions.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ivanfrias.myturn.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SubscriptionController extends ControllerUtils implements SubscriptionsApi {

    private final SubscriptionService subscriptionService;

    @Override
    public ResponseEntity<SubscriptionDTO> createSubscription(CreateSubscriptionRequestDTO createSubscriptionRequestDTO) {
        if(!checkIsAdmin()){
            throw new UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        Long userId = getAllClaims().get("user_id", Long.class);

        SubscriptionDTO subscriptionDTO = subscriptionService.createSubscription(
                createSubscriptionRequestDTO.getUserId(),
                createSubscriptionRequestDTO.getStartDate(),
                createSubscriptionRequestDTO.getDurationInMonths(),
                userId
        );
        return ResponseEntity.created(null).body(subscriptionDTO);
    }

    @Override
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByUserId(Long userId) {
        if(!checkIsAdmin()){
            throw new UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        Long ownerId = getAllClaims().get("user_id", Long.class);
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId, ownerId));
    }

    @Override
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(Long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(subscriptionId));
    }
}
