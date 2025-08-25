package com.ivanfrias.myturn.users.controllers;

import com.ivanfrias.myturn.api.UsersApi;
import com.ivanfrias.myturn.common.exceptions.utils.ControllerUtils;
import com.ivanfrias.myturn.common.exceptions.utils.UnauthorizedException;
import com.ivanfrias.myturn.companies.services.CompanyUserService;
import com.ivanfrias.myturn.model.UserDTO;
import com.ivanfrias.myturn.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ivanfrias.myturn.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController extends ControllerUtils implements UsersApi {
    private final UserService userService;
    private final CompanyUserService companyUserService;

    @Override
    public ResponseEntity<UserDTO> getSelfUser() {
        Long userId = getAllClaims().get("user_id", Long.class);
        return ResponseEntity.ok(userService.getUserDTOById(userId));
    }

    @Override
    public ResponseEntity<Void> linkUserToCompany(String linkedCode) {
        if(!checkIsUser()){
            throw new UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        Long userId = getAllClaims().get("user_id", Long.class);
        companyUserService.linkUserToCompany(userId, linkedCode);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserDTO>> getUsersByOwner() {
        if(!checkIsAdmin()){
            throw new UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        Long ownerId = getAllClaims().get("user_id", Long.class);
        return ResponseEntity.ok(userService.getUsersByOwner(ownerId));
    }

    @Override
    public ResponseEntity<String> getTest() {
        return ResponseEntity.ok("Esto es un simple test con CD automatico");
    }
}
