package com.ivanfrias.myturn.companies.services;

import com.ivanfrias.myturn.common.exceptions.ConflictException;
import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.companies.dao.repositories.CompanyRepository;
import com.ivanfrias.myturn.model.CompanyDTO;
import com.ivanfrias.myturn.model.CreateCompanyRequestDTO;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private CompanyEntity getCompanyEntityById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));
    }

    public CompanyDTO createCompany(CreateCompanyRequestDTO createCompany) {
        CompanyEntity company = companyRepository.findByOwner_id(createCompany.getOwnerId()).orElse(null);
        UserEntity user = userService.getUserEntityById(createCompany.getOwnerId());

        if (Objects.nonNull(company)) {
            throw new ConflictException("Este usuario ya tiene una company asignada");
        }

        CompanyEntity companyToSave = CompanyEntity.builder()
                .name(createCompany.getName())
                .owner(user)
                .linkCode(createLinkCode())
                .build();

        CompanyEntity currentCompany = companyRepository.save(companyToSave);

        return modelMapper.map(currentCompany, CompanyDTO.class);
    }

    public CompanyDTO getCompanyById(Long companyId) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));

        return modelMapper.map(company, CompanyDTO.class);
    }

    private String createLinkCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
