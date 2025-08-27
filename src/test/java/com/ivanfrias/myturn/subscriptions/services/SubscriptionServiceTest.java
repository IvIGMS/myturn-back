package com.ivanfrias.myturn.subscriptions.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.ivanfrias.myturn.common.exceptions.ConflictException;
import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.companies.services.CompanyService;
import com.ivanfrias.myturn.model.SubscriptionDTO;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.security.dao.models.enums.RoleEnum;
import com.ivanfrias.myturn.subscriptions.dao.models.entities.SubscriptionEntity;
import com.ivanfrias.myturn.subscriptions.dao.repositories.SubscriptionRepository;
import com.ivanfrias.myturn.users.services.UserService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

  @InjectMocks private SubscriptionService subscriptionService;

  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private UserService userService;
  @Mock private CompanyService companyService;

  private UserEntity userEntity;
  private CompanyEntity companyEntity;

  @BeforeEach
  void setUp() {
    companyEntity = new CompanyEntity();
    companyEntity.setId(10L);

    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setRole(RoleEnum.USER);
    userEntity.setCompany(companyEntity);
  }

  @Nested
  @DisplayName("CreateSubscription Tests")
  class CreateSubscriptionTests {

    @Test
    @DisplayName("Should create a new subscription for a user")
    void createSubscription_forNewUser_shouldCreateSubscription() {
      when(userService.getUserEntityById(1L)).thenReturn(userEntity);
      when(companyService.findCompanyEntityByOwnerId(100L)).thenReturn(companyEntity);
      when(subscriptionRepository.isAvailableYet(anyLong(), any(LocalDate.class))).thenReturn(null);
      when(subscriptionRepository.save(any(SubscriptionEntity.class)))
          .thenAnswer(i -> i.getArguments()[0]);
      when(modelMapper.map(any(SubscriptionEntity.class), eq(SubscriptionDTO.class)))
          .thenReturn(new SubscriptionDTO());

      subscriptionService.createSubscription(1L, LocalDate.now(), 1, 100L);

      ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
      verify(subscriptionRepository).save(captor.capture());
      assertEquals(LocalDate.now(), captor.getValue().getStartDate());
      assertTrue(captor.getValue().getIsActive());
    }

    @Test
    @DisplayName("Should chain subscription if one is already active")
    void createSubscription_forUserWithExistingSubscription_shouldChainSubscription() {
      LocalDate lastEndDate = LocalDate.now().plusMonths(1);
      when(userService.getUserEntityById(1L)).thenReturn(userEntity);
      when(companyService.findCompanyEntityByOwnerId(100L)).thenReturn(companyEntity);
      when(subscriptionRepository.isAvailableYet(anyLong(), any(LocalDate.class)))
          .thenReturn(lastEndDate);
      when(subscriptionRepository.save(any(SubscriptionEntity.class)))
          .thenAnswer(i -> i.getArguments()[0]);
      when(modelMapper.map(any(SubscriptionEntity.class), eq(SubscriptionDTO.class)))
          .thenReturn(new SubscriptionDTO());

      subscriptionService.createSubscription(1L, LocalDate.now(), 1, 100L);

      ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
      verify(subscriptionRepository).save(captor.capture());
      assertEquals(lastEndDate.plusDays(1), captor.getValue().getStartDate());
    }

    @Test
    @DisplayName("Should throw ConflictException for zero duration")
    void createSubscription_withZeroDuration_shouldThrowConflictException() {
      assertThrows(
          ConflictException.class,
          () -> subscriptionService.createSubscription(1L, LocalDate.now(), 0, 100L));
    }

    @Test
    @DisplayName("Should throw ConflictException if user has no company")
    void createSubscription_whenUserHasNoCompany_shouldThrowConflictException() {
      userEntity.setCompany(null);
      when(userService.getUserEntityById(1L)).thenReturn(userEntity);
      when(companyService.findCompanyEntityByOwnerId(100L)).thenReturn(companyEntity);

      assertThrows(
          ConflictException.class,
          () -> subscriptionService.createSubscription(1L, LocalDate.now(), 1, 100L));
    }

    @Test
    @DisplayName("Should throw ConflictException if user not in owner's company")
    void createSubscription_whenUserNotInOwnersCompany_shouldThrowConflictException() {
      CompanyEntity anotherCompany = new CompanyEntity();
      anotherCompany.setId(99L);
      userEntity.setCompany(anotherCompany);
      when(userService.getUserEntityById(1L)).thenReturn(userEntity);
      when(companyService.findCompanyEntityByOwnerId(100L)).thenReturn(companyEntity);

      assertThrows(
          ConflictException.class,
          () -> subscriptionService.createSubscription(1L, LocalDate.now(), 1, 100L));
    }
  }

  @Nested
  @DisplayName("GetSubscriptionsByUserId Tests")
  class GetSubscriptionsByUserIdTests {
    @Test
    @DisplayName("Should return subscriptions for a valid user")
    void getSubscriptionsByUserId_whenUserIsValid_shouldReturnSubscriptions() {
      when(userService.getUserEntityById(1L)).thenReturn(userEntity);
      when(companyService.findCompanyEntityByOwnerId(100L)).thenReturn(companyEntity);
      when(subscriptionRepository.findByUserIdOrderByStartDateDesc(1L))
          .thenReturn(Collections.singletonList(new SubscriptionEntity()));

      List<SubscriptionDTO> result = subscriptionService.getSubscriptionsByUserId(1L, 100L);

      assertNotNull(result);
      assertFalse(result.isEmpty());
      verify(subscriptionRepository).findByUserIdOrderByStartDateDesc(1L);
    }

    @Test
    @DisplayName("Should throw ConflictException if user is not ROLE_USER")
    void getSubscriptionsByUserId_whenUserIsNotRoleUser_shouldThrowConflictException() {
      userEntity.setRole(RoleEnum.ADMIN);
      when(userService.getUserEntityById(1L)).thenReturn(userEntity);

      assertThrows(
          ConflictException.class, () -> subscriptionService.getSubscriptionsByUserId(1L, 100L));
    }
  }

  @Nested
  @DisplayName("GetSubscriptionById Tests")
  class GetSubscriptionByIdTests {
    @Test
    @DisplayName("Should return DTO when subscription exists")
    void getSubscriptionById_whenExists_shouldReturnDTO() {
      when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(new SubscriptionEntity()));
      when(modelMapper.map(any(SubscriptionEntity.class), eq(SubscriptionDTO.class)))
          .thenReturn(new SubscriptionDTO());

      SubscriptionDTO result = subscriptionService.getSubscriptionById(1L);

      assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when subscription does not exist")
    void getSubscriptionById_whenNotExists_shouldThrowNotFoundException() {
      when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> subscriptionService.getSubscriptionById(1L));
    }
  }

  @Nested
  @DisplayName("DisableSubscription Tests")
  class DisableSubscriptionTests {
    @Test
    @DisplayName("Should set subscriptions to inactive")
    void disableSubscription_whenSubscriptionsExist_shouldSetInactive() {
      SubscriptionEntity sub1 = new SubscriptionEntity();
      sub1.setIsActive(true);
      SubscriptionEntity sub2 = new SubscriptionEntity();
      sub2.setIsActive(true);

      when(subscriptionRepository.disableSubscriptionByCompanyAndUserId(10L, 1L))
          .thenReturn(List.of(1L, 2L));
      when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(sub1));
      when(subscriptionRepository.findById(2L)).thenReturn(Optional.of(sub2));

      subscriptionService.disableSubscription(10L, 1L);

      verify(subscriptionRepository, times(2)).findById(anyLong());
      assertFalse(sub1.getIsActive());
      assertFalse(sub2.getIsActive());
    }
  }
}
