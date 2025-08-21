package com.ivanfrias.myturn.users.services;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.model.UserDTO;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.security.dao.models.enums.RoleEnum;
import com.ivanfrias.myturn.users.dao.dto.UserDownDto;
import com.ivanfrias.myturn.users.dao.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserEntity getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    public UserDTO getUserDTOById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> getUsersByOwner(Long ownerId) {
        List<UserEntity> users = userRepository.getUsersByOwnerId(ownerId);
        return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }

    public List<UserEntity> getAdminUsers() {
        return userRepository.findByRole(RoleEnum.ADMIN);
    }

    public List<UserDownDto> usersDownToday() {
        LocalDate now = LocalDate.now();
        return userRepository.usersDownToday(now);
    }
}
