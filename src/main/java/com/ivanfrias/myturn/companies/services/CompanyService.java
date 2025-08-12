package com.ivanfrias.myturn.companies.services;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.companies.dao.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    private CompanyEntity getCompanyEntityById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));
    }
}
