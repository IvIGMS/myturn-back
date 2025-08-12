package com.ivanfrias.myturn.subscriptions.controllers;

import com.ivanfrias.myturn.api.SubscriptionsApi;
import com.ivanfrias.myturn.model.CreateSubscriptionRequestDTO;
import com.ivanfrias.myturn.model.SubscriptionDTO;
import com.ivanfrias.myturn.subscriptions.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SubscriptionController implements SubscriptionsApi {

    private final SubscriptionService subscriptionService;

    @Override
    public ResponseEntity<SubscriptionDTO> createSubscription(CreateSubscriptionRequestDTO createSubscriptionRequestDTO) {
        SubscriptionDTO subscriptionDTO = subscriptionService.createSubscription(
            createSubscriptionRequestDTO.getUserId(), 
            createSubscriptionRequestDTO.getStartDate(), 
            createSubscriptionRequestDTO.getDurationInMonths()
        );
        return ResponseEntity.ok(subscriptionDTO);
    }

    @Override
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByUserId(Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
    }
}
