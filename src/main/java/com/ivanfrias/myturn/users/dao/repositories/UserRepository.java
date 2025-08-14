package com.ivanfrias.myturn.users.dao.repositories;

import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query(value =
            " SELECT u.* from users u " +
            " INNER JOIN companies c ON c.id = u.company_id " +
            " WHERE c.owner_id = :ownerId",
            nativeQuery = true)
    public List<UserEntity> getUsersByOwnerId(@Param("ownerId") Long ownerId);
}

