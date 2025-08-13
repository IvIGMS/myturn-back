package com.ivanfrias.myturn.companies.controllers;

import com.ivanfrias.myturn.api.CompaniesApi;
import com.ivanfrias.myturn.common.exceptions.utils.ControllerUtils;
import com.ivanfrias.myturn.common.exceptions.utils.UnauthorizedException;
import com.ivanfrias.myturn.companies.services.CompanyService;
import com.ivanfrias.myturn.companies.services.CompanyUserService;
import com.ivanfrias.myturn.model.CompanyDTO;
import com.ivanfrias.myturn.model.CreateCompanyRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ivanfrias.myturn.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CompanyController extends ControllerUtils implements CompaniesApi {
    private final CompanyService companyService;
    private final CompanyUserService companyUserService;

    @Override
    public ResponseEntity<CompanyDTO> createCompany(CreateCompanyRequestDTO createCompany) {
        if(!checkIsAdmin()){
            throw new UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        Long userId = getAllClaims().get("user_id", Long.class);
        CompanyDTO companyDTO = companyUserService.createCompany(createCompany, userId);
        return ResponseEntity.created(null).body(companyDTO);

    }

    @Override
    public ResponseEntity<CompanyDTO> getCompanyById(Long companyId) {
        if(!checkIsAdmin()){
            throw new UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        return ResponseEntity.ok(companyService.getCompanyById(companyId));
    }

    @Override
    public ResponseEntity<CompanyDTO> getSelfCompany() {
        if(!checkIsAdmin()){
            throw new UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        Long userId = getAllClaims().get("user_id", Long.class);
        return ResponseEntity.ok(companyService.findCompanyByOwnerId(userId));
    }
}
