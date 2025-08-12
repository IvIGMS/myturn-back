package com.ivanfrias.myturn.companies.dao.repositories;

import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findByOwner_id(Long ownerId);
}

