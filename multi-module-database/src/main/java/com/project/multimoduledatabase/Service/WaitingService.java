package com.project.multimoduledatabase.Service;

import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.entity.CustomerEntity;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import com.project.multimoduledatabase.repository.CustomerRepository;
import com.project.multimoduledatabase.repository.RestaurantRepository;
import com.project.multimoduledatabase.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final RestaurantRepository restaurantRepository;
    private final WaitingRepository waitingRepository;
    private final CustomerRepository customerRepository;

    public WaitingRegisterRespDTO registerWaiting(Long restaurantId, Long customerId, int partySize) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));

        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        if (!isRestaurantOpen(restaurantEntity.getOpenTime(), restaurantEntity.getCloseTime(), LocalTime.now())) {
            throw new IllegalStateException("현재 영업 시간이 아닙니다.");
        }

        int isApplied = waitingRepository.countByCustomer_IdAndRestaurant_IdAndStatus(customerId, restaurantId, WaitingStatus.APPLIED);

        if (isApplied > 0) {
            throw new IllegalStateException("이미 등록되었습니다.");
        }

        int waitingNumber = waitingRepository.countByRegisteredDate(LocalDate.now()) + 1;

        WaitingEntity savedWaiting = waitingRepository.save(new WaitingEntity(
                customerEntity,
                restaurantEntity,
                WaitingStatus.APPLIED,
                partySize,
                waitingNumber,
                LocalDate.now(),
                LocalTime.now()));

        int remainingTeamCount = waitingRepository.countByRestaurantAndRegisteredDateAndStatusAndWaitingNumberLessThan(restaurantEntity,
                savedWaiting.getRegisteredDate(),
                WaitingStatus.APPLIED,
                waitingNumber);

        return new WaitingRegisterRespDTO(
                customerEntity.getId(),
                customerEntity.getName(),
                restaurantEntity.getId(),
                restaurantEntity.getName(),
                savedWaiting.getId(),
                savedWaiting.getWaitingNumber(),
                savedWaiting.getPartySize(),
                savedWaiting.getStatus(),
                remainingTeamCount,
                estimatedWaitingTime(remainingTeamCount),
                savedWaiting.getRegisteredDate(),
                savedWaiting.getRegisteredTime());
    }

    public WaitingCancelRespDTO cancelWaiting(Long waitingId) {
        WaitingEntity waitingEntity = waitingRepository.findByIdAndStatus(waitingId, WaitingStatus.APPLIED)
                .orElseThrow(() -> new IllegalArgumentException("이미 처리된 웨이팅입니다."));

        waitingEntity.updateStatus(WaitingStatus.CANCELED);

        return WaitingCancelRespDTO.from(waitingEntity);
    }

    @Transactional(readOnly = true)
    public WaitingOverviewDTO getWaitingOverview(Long restaurantId) {

        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        int remainingTeamCount = waitingRepository.countByRestaurantAndStatusAndRegisteredDate(
                restaurantEntity,
                WaitingStatus.APPLIED,
                LocalDate.now());

        return new WaitingOverviewDTO(
                restaurantId,
                restaurantEntity.getName(),
                estimatedWaitingTime(remainingTeamCount),
                remainingTeamCount);
    }

    @Transactional(readOnly = true)
    public MyWaitingStatusDTO getMyWaiting(Long restaurantId, Long waitingId, CommonWaitingReqDTO commonWaitingReq) {
        CustomerEntity customerEntity = customerRepository.findById(commonWaitingReq.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));
        WaitingEntity waitingEntity = waitingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException("웨이팅 등록 상태가 아닙니다."));

        int remainingTeamCount = waitingRepository.countByRestaurantAndRegisteredDateAndStatusAndWaitingNumberLessThan(restaurantEntity,
                waitingEntity.getRegisteredDate(),
                WaitingStatus.APPLIED,
                waitingEntity.getWaitingNumber());

        return new MyWaitingStatusDTO(
                commonWaitingReq.getCustomerId(),
                customerEntity.getName(),
                restaurantId,
                restaurantEntity.getName(),
                waitingEntity.getId(),
                waitingEntity.getWaitingNumber(),
                remainingTeamCount,
                WaitingStatus.APPLIED,
                estimatedWaitingTime(remainingTeamCount),
                waitingEntity.getRegisteredDate(),
                waitingEntity.getRegisteredTime());
    }

    public boolean isRestaurantOpen(String openTime, String closeTime, LocalTime now) {
        LocalTime open = LocalTime.parse(openTime);
        LocalTime close = LocalTime.parse(closeTime);
        return now.isAfter(open) && now.isBefore(close);
    }

    public String estimatedWaitingTime(int remainingTeamCount) {
        if (remainingTeamCount == 0) {
            return "현재 대기 팀이 없습니다.";
        } else if (remainingTeamCount <= 10) {
            return "약 30분 내 입장 가능";
        } else if (remainingTeamCount <= 20) {
            return "약 1시간 대기 예상";
        } else {
            return "약 2시간 이상 대기 예상";
        }
    }
}