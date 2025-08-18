package com.ivanfrias.myturn.users.dao.repositories;

import com.ivanfrias.myturn.security.dao.models.entities.UserEntity;
import com.ivanfrias.myturn.security.dao.models.enums.RoleEnum;
import com.ivanfrias.myturn.users.dao.dto.UserDownDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query(value =
            " SELECT u.* from users u " +
            " INNER JOIN companies c ON c.id = u.company_id " +
            " WHERE c.owner_id = :ownerId",
            nativeQuery = true)
    List<UserEntity> getUsersByOwnerId(@Param("ownerId") Long ownerId);

    List<UserEntity> findByRole(RoleEnum role);

    @Query(value = """
            SELECT
            u.email as email,
            u.firstname as name,
            u.lastname as lastName,
            s.company_id as companyId,
            s.user_id as userId
            FROM users u
            INNER JOIN subscriptions s ON s.user_id = u.id
            WHERE u.role = 'USER'
              AND s.end_date = (
                  SELECT MAX(s2.end_date)
                  FROM subscriptions s2
                  WHERE s2.user_id = u.id
              )
            AND s.end_date <= :now
            AND s.is_active = true
            """, nativeQuery = true)
    List<UserDownDto> usersDownToday(@Param("now")LocalDate now);
}

