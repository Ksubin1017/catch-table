package com.project.multimoduledatabase.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.multimoduledatabase.dto.RestaurantWaitingStatusOwnerDTO;
import com.project.multimoduledatabase.dto.WaitingCallReqDTO;
import com.project.multimoduledatabase.dto.WaitingOverviewOwnerDTO;
import com.project.multimoduledatabase.entity.CustomerEntity;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import com.project.multimoduledatabase.repository.RestaurantRepository;
import com.project.multimoduledatabase.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantOwnerService {

    private final RestaurantRepository restaurantRepository;
    private final WaitingRepository waitingRepository;
    private final SlackService slackService;

    public RestaurantWaitingStatusOwnerDTO getRestaurantWaitingStatusOwner(Long restaurantId) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Restaurant" + restaurantId));

        List<WaitingEntity> waitingList = waitingRepository.findByRestaurantIdAndStatusOrderByWaitingNumberAsc(restaurantId, WaitingStatus.APPLIED);

        List<WaitingOverviewOwnerDTO> waitingOverviews = waitingList.stream()
                .map(waitingEntity -> new WaitingOverviewOwnerDTO(
                        waitingEntity.getCustomer().getId(),
                        waitingEntity.getCustomer().getName(),
                        waitingEntity.getWaitingNumber(),
                        waitingEntity.getRegisteredDate(),
                        waitingEntity.getRegisteredTime()
                )).toList();

        return new RestaurantWaitingStatusOwnerDTO(
                restaurantId,
                restaurant.getName(),
                waitingRepository.countByRestaurantAndRegisteredDate(restaurant, LocalDate.now()),
                waitingRepository.countByRestaurantAndStatusAndRegisteredDate(restaurant, WaitingStatus.APPLIED, LocalDate.now()),
                waitingOverviews
        );
    }

    @Transactional
    public void callWaiting(WaitingCallReqDTO waitingCallReq) {
        int nextWaitingNumber = 0;
        WaitingEntity waitingEntity = waitingRepository.findById(waitingCallReq.getWaitingId())
                .orElseThrow(() -> new IllegalArgumentException("이미 처리된 웨이팅입니다."));

        CustomerEntity customer = waitingEntity.getCustomer();

        if (waitingEntity.getStatus().equals(WaitingStatus.CALLED)) {
            throw new IllegalStateException("이미 호명된 고객입니다.");
        } else if (waitingEntity.getStatus().equals(WaitingStatus.CANCELED)) {
            throw new IllegalStateException("대기 취소 고객입니다.");
        } else if (waitingEntity.getStatus().equals(WaitingStatus.COMPLETE)) {
            throw new IllegalStateException("이미 처리된 고객입니다.");
        }

        if(waitingRepository.findByRestaurantIdAndWaitingNumberAndStatus(waitingEntity.getRestaurant().getId(),
                waitingEntity.getWaitingNumber() + 1, WaitingStatus.APPLIED) != null) {
            nextWaitingNumber = waitingEntity.getWaitingNumber() + 1;
        }

        slackService.sendMessage(waitingEntity.getRestaurant(), waitingEntity.getWaitingNumber(), nextWaitingNumber, waitingEntity.getRestaurant().getWebhookUrl());
        waitingEntity.updateStatus(WaitingStatus.CALLED);
    }
}
