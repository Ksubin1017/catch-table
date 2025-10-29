package com.project.multimoduledatabase.repository;

import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface WaitingRepository extends JpaRepository<WaitingEntity, Long> {

    // 현재 대기팀 수
    int countByRestaurantIdAndStatusAndRegisteredAtLessThan(Long restaurantId,
                                                            WaitingStatus status,
                                                            LocalDateTime registeredAt);

    // 웨이팅 번호 지정
    @Query("SELECT MAX(w.waitingNumber) FROM WaitingEntity w " +
            "WHERE w.restaurant.id = :restaurantId " +
            "AND DATE(w.registeredAt) = :registeredDate " +
            "AND w.status = 'APPLIED'")
    Optional<Integer> findMaxWaitingNumberByRestaurantIdAndRegisteredAt(
            @Param("restaurantId") Long restaurantId,
            @Param("registeredDate") LocalDate registeredDate);

    WaitingEntity findByIdAndStatus(Long id,
                                    WaitingStatus status);

    @Query("SELECT COUNT(w) FROM WaitingEntity w " +
            "WHERE w.restaurant.id = :restaurantId " +
            "AND w.status = :status " +
            "AND w.registeredAt >= :startOfDay " +
            "AND w.registeredAt < :endOfDay")
    int countByRestaurantIdAndStatusAndDateRange(
            @Param("restaurantId") Long restaurantId,
            @Param("status") WaitingStatus status,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

}
