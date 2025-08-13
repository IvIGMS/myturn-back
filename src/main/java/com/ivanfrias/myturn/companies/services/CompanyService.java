package com.ivanfrias.myturn.companies.services;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.companies.dao.repositories.CompanyRepository;
import com.ivanfrias.myturn.model.CompanyDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    private CompanyEntity getCompanyEntityById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));
    }

    public CompanyEntity findCompanyEntityByOwnerId(Long ownerId) {
        return companyRepository.findByOwner_id(ownerId).orElse(null);
    }

    public CompanyDTO findCompanyByOwnerId(Long ownerId) {
        return modelMapper.map(findCompanyEntityByOwnerId(ownerId), CompanyDTO.class);
    }

    public CompanyDTO getCompanyById(Long companyId) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));

        return modelMapper.map(company, CompanyDTO.class);
    }

    public CompanyEntity save(CompanyEntity company) {
        return companyRepository.save(company);
    }

    public CompanyEntity getCompanyByLinkedCode(String linkCode) {
        return companyRepository.findByLinkCode(linkCode)
                .orElseThrow(() -> new NotFoundException("Company not found by linked code: " + linkCode));
    }
}
