package com.ivanfrias.myturn.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.model.UserDTO;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.security.dao.models.enums.RoleEnum;
import com.ivanfrias.myturn.users.dao.dto.UserDownDto;
import com.ivanfrias.myturn.users.dao.repositories.UserRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks private UserService userService;

  @Mock private UserRepository userRepository;

  @Mock private ModelMapper modelMapper;

  private UserEntity userEntity;
  private UserDTO userDTO;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setFirstname("John");
    userEntity.setLastname("Doe");
    userEntity.setRole(RoleEnum.USER);

    userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setFirstname("John");
    userDTO.setLastname("Doe");
  }

  @Nested
  @DisplayName("getUserEntityById Tests")
  class GetUserEntityByIdTests {

    @Test
    @DisplayName("Should return UserEntity when user exists")
    void getUserEntityById_whenUserExists_thenReturnUserEntity() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

      UserEntity result = userService.getUserEntityById(1L);

      assertNotNull(result);
      assertEquals(userEntity.getId(), result.getId());
      verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when user does not exist")
    void getUserEntityById_whenUserDoesNotExist_thenThrowNotFoundException() {
      when(userRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> userService.getUserEntityById(1L));
      verify(userRepository).findById(1L);
    }
  }

  @Nested
  @DisplayName("getUserDTOById Tests")
  class GetUserDTOByIdTests {

    @Test
    @DisplayName("Should return UserDTO when user exists")
    void getUserDTOById_whenUserExists_thenReturnUserDTO() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
      when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(userDTO);

      UserDTO result = userService.getUserDTOById(1L);

      assertNotNull(result);
      assertEquals(userDTO.getId(), result.getId());
      verify(userRepository).findById(1L);
      verify(modelMapper).map(userEntity, UserDTO.class);
    }

    @Test
    @DisplayName("Should throw NotFoundException when user does not exist")
    void getUserDTOById_whenUserDoesNotExist_thenThrowNotFoundException() {
      when(userRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> userService.getUserDTOById(1L));
      verify(userRepository).findById(1L);
    }
  }

  @Nested
  @DisplayName("getUsersByOwner Tests")
  class GetUsersByOwnerTests {

    @Test
    @DisplayName("Should return list of UserDTO when owner has users")
    void getUsersByOwner_whenUsersExist_thenReturnUserDTOList() {
      when(userRepository.getUsersByOwnerId(1L)).thenReturn(Collections.singletonList(userEntity));
      when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(userDTO);

      List<UserDTO> result = userService.getUsersByOwner(1L);

      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertEquals(1, result.size());
      assertEquals(userDTO.getId(), result.get(0).getId());
      verify(userRepository).getUsersByOwnerId(1L);
      verify(modelMapper).map(userEntity, UserDTO.class);
    }

    @Test
    @DisplayName("Should return empty list when owner has no users")
    void getUsersByOwner_whenNoUsers_thenReturnEmptyList() {
      when(userRepository.getUsersByOwnerId(1L)).thenReturn(Collections.emptyList());

      List<UserDTO> result = userService.getUsersByOwner(1L);

      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(userRepository).getUsersByOwnerId(1L);
    }
  }

  @Nested
  @DisplayName("getAdminUsers Tests")
  class GetAdminUsersTests {

    @Test
    @DisplayName("Should return list of admin UserEntity when admins exist")
    void getAdminUsers_whenAdminsExist_thenReturnUserEntityList() {
      userEntity.setRole(RoleEnum.ADMIN);
      when(userRepository.findByRole(RoleEnum.ADMIN))
          .thenReturn(Collections.singletonList(userEntity));

      List<UserEntity> result = userService.getAdminUsers();

      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertEquals(1, result.size());
      assertEquals(RoleEnum.ADMIN, result.get(0).getRole());
      verify(userRepository).findByRole(RoleEnum.ADMIN);
    }

    @Test
    @DisplayName("Should return empty list when no admins exist")
    void getAdminUsers_whenNoAdmins_thenReturnEmptyList() {
      when(userRepository.findByRole(RoleEnum.ADMIN)).thenReturn(Collections.emptyList());

      List<UserEntity> result = userService.getAdminUsers();

      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(userRepository).findByRole(RoleEnum.ADMIN);
    }
  }

  @Nested
  @DisplayName("usersDownToday Tests")
  class UsersDownTodayTests {

    @Test
    @DisplayName("Should return list of UserDownDto when users are down today")
    void usersDownToday_whenUsersDownExist_thenReturnUserDownDtoList() {
      UserDownDto userDownDto =
          new UserDownDto() {
            public String getNombre() {
              return "Test";
            }

            public String getApellidos() {
              return "User";
            }
          };
      when(userRepository.usersDownToday(any(LocalDate.class)))
          .thenReturn(Collections.singletonList(userDownDto));

      List<UserDownDto> result = userService.usersDownToday();

      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertEquals(1, result.size());
      verify(userRepository).usersDownToday(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should return empty list when no users are down today")
    void usersDownToday_whenNoUsersDown_thenReturnEmptyList() {
      when(userRepository.usersDownToday(any(LocalDate.class))).thenReturn(Collections.emptyList());

      List<UserDownDto> result = userService.usersDownToday();

      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(userRepository).usersDownToday(any(LocalDate.class));
    }
  }
}
