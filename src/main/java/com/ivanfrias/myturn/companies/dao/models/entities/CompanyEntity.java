package com.ivanfrias.myturn.companies.dao.models.entities;

import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private UserEntity owner;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<UserEntity> users = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String linkCode;
}
