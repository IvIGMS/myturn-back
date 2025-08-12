package com.ivanfrias.myturn.companies.controllers;

import com.ivanfrias.myturn.api.CompaniesApi;
import com.ivanfrias.myturn.companies.services.CompanyService;
import com.ivanfrias.myturn.model.CompanyDTO;
import com.ivanfrias.myturn.model.CreateCompanyRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CompanyController implements CompaniesApi {
    private final CompanyService companyService;

    @Override
    public ResponseEntity<CompanyDTO> createCompany(CreateCompanyRequestDTO createCompany) {
        CompanyDTO companyDTO = companyService.createCompany(createCompany);
        return ResponseEntity.created(null).body(companyDTO);

    }

    @Override
    public ResponseEntity<CompanyDTO> getCompanyById(Long companyId) {
        return ResponseEntity.ok(companyService.getCompanyById(companyId));
    }
}
