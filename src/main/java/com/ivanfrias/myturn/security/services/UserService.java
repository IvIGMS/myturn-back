package com.ivanfrias.myturn.security.services;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.model.UserDTO;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.security.dao.models.enums.RoleEnum;
import com.ivanfrias.myturn.security.dao.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Value("${activate.user.mocked}")
    private boolean isUserMocked;

    public UserEntity getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    public UserDTO getUserDTOById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> getUsers() {
        List<UserEntity> entityUsers = userRepository.findAllByRole(RoleEnum.USER);
        if(entityUsers.isEmpty()){
            throw new NotFoundException("No hay ningún usuario con role USER registrado");
        }
        return entityUsers.stream()
                .map(ue -> modelMapper.map(ue, UserDTO.class))
                .toList();
    }
}
