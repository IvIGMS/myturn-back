package com.ivanfrias.myturn.users.controllers;

import com.ivanfrias.myturn.common.exceptions.utils.UnauthorizedException;
import com.ivanfrias.myturn.companies.services.CompanyUserService;
import com.ivanfrias.myturn.model.UserDTO;
import com.ivanfrias.myturn.users.controllers.common.AbstractControllerTest;
import com.ivanfrias.myturn.users.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest extends AbstractControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    @Mock
    CompanyUserService companyUserService;


    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(userController, "request", request);
        ReflectionTestUtils.setField(userController, "jwtService", jwtService);
    }

    @Test
    void testGetSelfUser() {
        mockAuthenticatedUser(1L, null);

        var userDto = UserDTO.builder()
                .id(1L)
                .firstname("Ronaldo")
                .lastname("Nazario")
                .build();

        when(userService.getUserDTOById(1L)).thenReturn(userDto);

        var results = userController.getSelfUser();

        assertNotNull(results);
        assertNotNull(results.getBody());
        assertEquals("Ronaldo", results.getBody().getFirstname());
        assertEquals("Nazario", results.getBody().getLastname());
    }

    @Test
    void testLinkUserToCompany_ok() {
        mockAuthenticatedUser(1L, "USER");

        doNothing().when(companyUserService).linkUserToCompany(anyLong(), anyString());

        var results = userController.linkUserToCompany("dNdb24ksd");
        assertNotNull(results);
    }

    @Test
    void testLinkUserToCompany_ko() {
        mockAuthenticatedUser(1L, "ADMIN");

        assertThrows(
                UnauthorizedException.class,
                () -> userController.linkUserToCompany("dNdb24ksd")
        );
    }

    @Test
    void testGetUsersByOwner_ok() {
        mockAuthenticatedUser(1L, "ADMIN");

        var users = List.of(UserDTO.builder().id(1L).firstname("Dani").lastname("Carvajal").build());

        when(userService.getUsersByOwner(anyLong())).thenReturn(users);

        var results = userController.getUsersByOwner();
        assertNotNull(results);
        assertNotNull(results.getBody());
        assertEquals("Dani", results.getBody().get(0).getFirstname());
        assertEquals("Carvajal", results.getBody().get(0).getLastname());
    }

    @Test
    void testGetUsersByOwner_ko() {
        mockAuthenticatedUser(1L, "USER");
        assertThrows(UnauthorizedException.class, () -> userController.getUsersByOwner());
    }

    // Esta clase ya es un test en sí misma.
    @Test
    void testGetTest() {
        var results = userController.getTest();
        assertNotNull(results);
    }
}
