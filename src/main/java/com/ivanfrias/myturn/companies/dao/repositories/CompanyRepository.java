package com.ivanfrias.myturn.companies.dao.repositories;

import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
  Optional<CompanyEntity> findByOwner_id(Long ownerId);

  Optional<CompanyEntity> findByLinkCode(String linkCode);
}
