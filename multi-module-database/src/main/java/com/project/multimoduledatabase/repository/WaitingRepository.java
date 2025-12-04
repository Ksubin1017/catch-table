package com.project.multimoduledatabase.repository;

import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingRepository extends JpaRepository<WaitingEntity, Long> {

    // 현재 대기 팀 수 - 등록 시
    int countByRestaurantAndRegisteredDateAndStatusAndWaitingNumberLessThan(
            RestaurantEntity restaurant,
            LocalDate registeredDate,
            WaitingStatus status,
            int waitingNumber
    );

    // 현재 대기 팀 수 - OverView 시
    int countByRestaurantAndStatusAndRegisteredDate(RestaurantEntity restaurant, WaitingStatus status, LocalDate registeredDate);

    // 웨이팅 번호 발급
    int countByRegisteredDate(LocalDate date);

    Optional<WaitingEntity> findByIdAndStatus(Long waitingId, WaitingStatus waitingStatus);
    WaitingEntity findByCustomer_Id(Long customerId);

    int countByCustomer_IdAndRestaurant_IdAndStatus(Long customerId, Long restaurantId, WaitingStatus waitingStatus);

    // 하루 총 대기 팀 수
    int countByRestaurantAndRegisteredDate(RestaurantEntity restaurant, LocalDate registeredDate);

    List<WaitingEntity> findByRestaurantIdAndStatusOrderByWaitingNumberAsc(Long restaurantId, WaitingStatus status);

    WaitingEntity findByRestaurantIdAndWaitingNumberAndStatus(Long restaurantId, int waitingNumber, WaitingStatus status);
}