package com.ivanfrias.myturn.subscriptions.dao.repositories;

import com.ivanfrias.myturn.subscriptions.dao.models.entities.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    List<SubscriptionEntity> findByUserId(Long userId);

    List<SubscriptionEntity> findByUserIdOrderByStartDateDesc(Long userId);

    @Query(value = """
            select s.end_date from subscriptions s\s
            where s.user_id = :userId
            and s.end_date >= :currentDate
            order by s.end_date desc
            limit 1
    """, nativeQuery = true)
    LocalDate isAvailableYet(@Param("userId") Long userId,
                           @Param("currentDate") LocalDate currentDate
    );

    @Query(value = """
            select s.id from subscriptions s
            where s.company_id = :companyId
            and s.user_id = :userId
            and s.is_active = true
    """, nativeQuery = true)
    List<Long> disableSubscriptionByCompanyAndUserId(
            @Param("companyId") Long CompanyId,
            @Param("userId") Long userId
    );

}

