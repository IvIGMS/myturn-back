package com.ivanfrias.myturn.companies.services;

import com.ivanfrias.myturn.common.exceptions.ConflictException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.model.CompanyDTO;
import com.ivanfrias.myturn.model.CreateCompanyRequestDTO;
import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.users.services.UserService;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyUserService {
  private final CompanyService companyService;
  private final UserService userService;
  private final ModelMapper modelMapper;

  public CompanyDTO createCompany(CreateCompanyRequestDTO createCompany, Long userId) {
    CompanyEntity company = companyService.findCompanyEntityByOwnerId(userId);
    UserEntity user = userService.getUserEntityById(userId);

    if (Objects.nonNull(company)) {
      throw new ConflictException("Este usuario ya tiene una company asignada");
    }

    CompanyEntity companyToSave =
        CompanyEntity.builder()
            .name(createCompany.getName())
            .owner(user)
            .linkCode(createLinkCode())
            .build();

    CompanyEntity currentCompany = companyService.save(companyToSave);

    return modelMapper.map(currentCompany, CompanyDTO.class);
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

  @Transactional
  public void linkUserToCompany(Long userId, String linkedCode) {
    UserEntity user = userService.getUserEntityById(userId);
    CompanyEntity company = companyService.getCompanyByLinkedCode(linkedCode);
    user.setCompany(company);
  }
}
