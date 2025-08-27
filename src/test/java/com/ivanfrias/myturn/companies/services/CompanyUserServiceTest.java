package com.ivanfrias.myturn.companies.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.ivanfrias.myturn.common.exceptions.ConflictException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.model.CompanyDTO;
import com.ivanfrias.myturn.model.CreateCompanyRequestDTO;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.users.services.UserService;
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
class CompanyUserServiceTest {

  @InjectMocks private CompanyUserService companyUserService;

  @Mock private CompanyService companyService;
  @Mock private UserService userService;
  @Mock private ModelMapper modelMapper;

  @Nested
  @DisplayName("CreateCompany Tests")
  class CreateCompanyTests {

    @Test
    @DisplayName("Should create a company when user does not have one")
    void createCompany_whenUserHasNoCompany_shouldCreateCompany() {
      // Arrange
      CreateCompanyRequestDTO request = new CreateCompanyRequestDTO();
      request.setName("New Corp");
      UserEntity user = new UserEntity();
      user.setId(1L);

      when(companyService.findCompanyEntityByOwnerId(1L)).thenReturn(null);
      when(userService.getUserEntityById(1L)).thenReturn(user);
      when(companyService.save(any(CompanyEntity.class))).thenAnswer(i -> i.getArguments()[0]);
      when(modelMapper.map(any(CompanyEntity.class), eq(CompanyDTO.class)))
          .thenReturn(new CompanyDTO());

      // Act
      companyUserService.createCompany(request, 1L);

      // Assert
      verify(companyService).findCompanyEntityByOwnerId(1L);
      verify(userService).getUserEntityById(1L);

      ArgumentCaptor<CompanyEntity> companyCaptor = ArgumentCaptor.forClass(CompanyEntity.class);
      verify(companyService).save(companyCaptor.capture());
      CompanyEntity savedCompany = companyCaptor.getValue();

      assertEquals("New Corp", savedCompany.getName());
      assertEquals(user, savedCompany.getOwner());
      assertNotNull(savedCompany.getLinkCode());
      assertEquals(10, savedCompany.getLinkCode().length());

      verify(modelMapper).map(any(CompanyEntity.class), eq(CompanyDTO.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when user already has a company")
    void createCompany_whenUserAlreadyHasCompany_shouldThrowConflictException() {
      // Arrange
      when(companyService.findCompanyEntityByOwnerId(1L)).thenReturn(new CompanyEntity());

      // Act & Assert
      assertThrows(
          ConflictException.class,
          () -> companyUserService.createCompany(new CreateCompanyRequestDTO(), 1L));
      verify(userService, times(1)).getUserEntityById(anyLong());
    }
  }

  @Nested
  @DisplayName("LinkUserToCompany Tests")
  class LinkUserToCompanyTests {

    @Test
    @DisplayName("Should set company on user when user and company exist")
    void linkUserToCompany_whenUserAndCompanyExist_shouldLinkThem() {
      // Arrange
      UserEntity user = new UserEntity();
      user.setId(1L);
      assertNull(user.getCompany()); // Pre-condition

      CompanyEntity company = new CompanyEntity();
      company.setId(10L);

      when(userService.getUserEntityById(1L)).thenReturn(user);
      when(companyService.getCompanyByLinkedCode("link-code")).thenReturn(company);

      // Act
      companyUserService.linkUserToCompany(1L, "link-code");

      // Assert
      verify(userService).getUserEntityById(1L);
      verify(companyService).getCompanyByLinkedCode("link-code");
      assertNotNull(user.getCompany());
      assertEquals(10L, user.getCompany().getId());
    }
  }
}
