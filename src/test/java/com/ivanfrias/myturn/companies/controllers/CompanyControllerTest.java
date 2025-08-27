package com.ivanfrias.myturn.companies.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ivanfrias.myturn.common.exceptions.utils.UnauthorizedException;
import com.ivanfrias.myturn.companies.services.CompanyService;
import com.ivanfrias.myturn.companies.services.CompanyUserService;
import com.ivanfrias.myturn.model.CompanyDTO;
import com.ivanfrias.myturn.model.CreateCompanyRequestDTO;
import com.ivanfrias.myturn.users.controllers.common.AbstractControllerTest;
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
class CompanyControllerTest extends AbstractControllerTest {

  @InjectMocks private CompanyController companyController;

  @Mock private CompanyService companyService;

  @Mock private CompanyUserService companyUserService;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(companyController, "request", request);
    ReflectionTestUtils.setField(companyController, "jwtService", jwtService);
  }

  @Nested
  @DisplayName("createCompany Tests")
  class CreateCompanyTests {

    @Test
    @DisplayName("Should create a company when user is admin")
    void createCompany_asAdmin_shouldCreateCompany() {
      mockAuthenticatedUser(1L, "ADMIN");
      CreateCompanyRequestDTO requestDTO = new CreateCompanyRequestDTO();
      CompanyDTO companyDTO = new CompanyDTO();
      companyDTO.setId(1L);

      when(companyUserService.createCompany(any(CreateCompanyRequestDTO.class), anyLong()))
          .thenReturn(companyDTO);

      ResponseEntity<CompanyDTO> response = companyController.createCompany(requestDTO);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not admin")
    void createCompany_asNonAdmin_shouldThrowUnauthorizedException() {
      mockAuthenticatedUser(1L, "USER");
      CreateCompanyRequestDTO requestDTO = new CreateCompanyRequestDTO();

      assertThrows(UnauthorizedException.class, () -> companyController.createCompany(requestDTO));
    }
  }

  @Nested
  @DisplayName("getCompanyById Tests")
  class GetCompanyByIdTests {

    @Test
    @DisplayName("Should return a company when user is admin")
    void getCompanyById_asAdmin_shouldReturnCompany() {
      mockAuthenticatedUser(1L, "ADMIN");
      CompanyDTO companyDTO = new CompanyDTO();
      companyDTO.setId(1L);

      when(companyService.getCompanyById(1L)).thenReturn(companyDTO);

      ResponseEntity<CompanyDTO> response = companyController.getCompanyById(1L);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not admin")
    void getCompanyById_asNonAdmin_shouldThrowUnauthorizedException() {
      mockAuthenticatedUser(1L, "USER");

      assertThrows(UnauthorizedException.class, () -> companyController.getCompanyById(1L));
    }
  }

  @Nested
  @DisplayName("getSelfCompany Tests")
  class GetSelfCompanyTests {

    @Test
    @DisplayName("Should return self company when user is admin")
    void getSelfCompany_asAdmin_shouldReturnOwnCompany() {
      mockAuthenticatedUser(1L, "ADMIN");
      CompanyDTO companyDTO = new CompanyDTO();
      companyDTO.setId(1L);

      when(companyService.findCompanyByOwnerId(1L)).thenReturn(companyDTO);

      ResponseEntity<CompanyDTO> response = companyController.getSelfCompany();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not admin")
    void getSelfCompany_asNonAdmin_shouldThrowUnauthorizedException() {
      mockAuthenticatedUser(1L, "USER");

      assertThrows(UnauthorizedException.class, () -> companyController.getSelfCompany());
    }
  }
}
