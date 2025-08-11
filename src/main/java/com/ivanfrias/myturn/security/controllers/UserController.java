package com.ivanfrias.myturn.security.controllers;

import com.ivanfrias.myturn.api.UsersApi;
import com.ivanfrias.myturn.common.exceptions.utils.ControllerUtils;
import com.ivanfrias.myturn.model.UserDTO;
import com.ivanfrias.myturn.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController extends ControllerUtils implements UsersApi {
    private final UserService userService;

    @Override
    public ResponseEntity<UserDTO> getSelfUser() {
        Long userId = getAllClaims().get("user_id", Long.class);
        return ResponseEntity.ok(userService.getUserDTOById(userId));
    }
}
