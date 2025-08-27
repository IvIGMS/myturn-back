package com.ivanfrias.myturn.subscriptions.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.ivanfrias.myturn.common.exceptions.utils.UnauthorizedException;
import com.ivanfrias.myturn.model.CreateSubscriptionRequestDTO;
import com.ivanfrias.myturn.model.SubscriptionDTO;
import com.ivanfrias.myturn.subscriptions.services.SubscriptionService;
import com.ivanfrias.myturn.users.controllers.common.AbstractControllerTest;
import java.time.LocalDate;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest extends AbstractControllerTest {

  @InjectMocks private SubscriptionController subscriptionController;

  @Mock private SubscriptionService subscriptionService;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(subscriptionController, "request", request);
    ReflectionTestUtils.setField(subscriptionController, "jwtService", jwtService);
  }

  @Nested
  @DisplayName("CreateSubscription Tests")
  class CreateSubscriptionTests {

    @Test
    @DisplayName("Should create a subscription when user is admin")
    void createSubscription_asAdmin_shouldCreateSubscription() {
      mockAuthenticatedUser(1L, "ADMIN");
      CreateSubscriptionRequestDTO requestDTO = new CreateSubscriptionRequestDTO();
      requestDTO.setUserId(2L);
      requestDTO.setStartDate(LocalDate.now());
      requestDTO.setDurationInMonths(1);

      SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
      subscriptionDTO.setId(1L);

      when(subscriptionService.createSubscription(
              anyLong(), any(LocalDate.class), anyInt(), anyLong()))
          .thenReturn(subscriptionDTO);

      ResponseEntity<SubscriptionDTO> response =
          subscriptionController.createSubscription(requestDTO);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not admin")
    void createSubscription_asNonAdmin_shouldThrowUnauthorizedException() {
      mockAuthenticatedUser(1L, "USER");
      CreateSubscriptionRequestDTO requestDTO = new CreateSubscriptionRequestDTO();

      assertThrows(
          UnauthorizedException.class, () -> subscriptionController.createSubscription(requestDTO));
    }
  }

  @Nested
  @DisplayName("GetSubscriptionsByUserId Tests")
  class GetSubscriptionsByUserIdTests {

    @Test
    @DisplayName("Should return subscriptions when user is admin")
    void getSubscriptionsByUserId_asAdmin_shouldReturnSubscriptions() {
      mockAuthenticatedUser(1L, "ADMIN");
      SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
      subscriptionDTO.setId(1L);

      when(subscriptionService.getSubscriptionsByUserId(2L, 1L))
          .thenReturn(Collections.singletonList(subscriptionDTO));

      ResponseEntity<List<SubscriptionDTO>> response =
          subscriptionController.getSubscriptionsByUserId(2L);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertFalse(response.getBody().isEmpty());
      assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not admin")
    void getSubscriptionsByUserId_asNonAdmin_shouldThrowUnauthorizedException() {
      mockAuthenticatedUser(1L, "USER");

      assertThrows(
          UnauthorizedException.class, () -> subscriptionController.getSubscriptionsByUserId(2L));
    }
  }

  @Nested
  @DisplayName("GetSubscriptionById Tests")
  class GetSubscriptionByIdTests {

    @Test
    @DisplayName("Should return subscription for any authenticated user")
    void getSubscriptionById_whenSubscriptionExists_shouldReturnSubscription() {
      SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
      subscriptionDTO.setId(1L);

      when(subscriptionService.getSubscriptionById(1L)).thenReturn(subscriptionDTO);

      ResponseEntity<SubscriptionDTO> response = subscriptionController.getSubscriptionById(1L);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1L, response.getBody().getId());
    }
  }
}
