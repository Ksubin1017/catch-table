package com.project.multimoduledatabase.Service;

import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.entity.CustomerEntity;
import com.project.multimoduledatabase.entity.MenuEntity;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import com.project.multimoduledatabase.repository.CustomerRepository;
import com.project.multimoduledatabase.repository.MenuRepository;
import com.project.multimoduledatabase.repository.RestaurantRepository;
import com.project.multimoduledatabase.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final WaitingRepository waitingRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public RestaurantListDTO getRestaurantList(String category){
        List<RestaurantEntity> restaurantEntityList = restaurantRepository.findByCategory(category);

        List<restaurantItemListDTO> restaurantItemList = new ArrayList<>();

        for(RestaurantEntity restaurantEntity : restaurantEntityList){
            restaurantItemListDTO restaurantItem = restaurantItemListDTO.builder()
                    .restaurantId(restaurantEntity.getId())
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .restaurantAddr(restaurantEntity.getRestaurantAddr())
                    .restaurantImage(restaurantEntity.getRestaurantImage())
                    .category(restaurantEntity.getCategory())
                    .price(restaurantEntity.getPrice())
                    .businessHours(BusinessHoursDTO.builder()
                            .open(restaurantEntity.getOpenTime())
                            .close(restaurantEntity.getCloseTime())
                            .build())
                    .isOpen(isRestaurantOpen(restaurantEntity.getOpenTime(), restaurantEntity.getCloseTime(), LocalTime.now()))
                    .build();
            restaurantItemList.add(restaurantItem);
        }


        return RestaurantListDTO.builder()
                .restaurantItems(restaurantItemList)
                .build();
    }

    @Transactional(readOnly = true)
    public RestaurantDetailDTO getRestaurantDetail(Long restaurantId){
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));
        List<MenuEntity> menuEntity = menuRepository.findByRestaurant_RestaurantId(restaurantId);

        List<MenuDTO> menuList = new ArrayList<>();

        for(MenuEntity menuItem : menuEntity){
            MenuDTO menu = MenuDTO.builder()
                    .id(menuItem.getId())
                    .menuName(menuItem.getMenuName())
                    .menuPrice(menuItem.getMenuPrice())
                    .menuImage(menuItem.getMenuImage())
                    .build();

            menuList.add(menu);
        }

        return RestaurantDetailDTO.builder()
                .restaurantId(restaurantId)
                .restaurantImage(restaurantEntity.getRestaurantImage())
                .restaurantName(restaurantEntity.getRestaurantName())
                .restaurantAddr(restaurantEntity.getRestaurantAddr())
                .menuList(menuList)
                .businessHours(BusinessHoursDTO.builder()
                        .open(restaurantEntity.getOpenTime())
                        .close(restaurantEntity.getCloseTime())
                        .build())
                .isOpen(isRestaurantOpen(restaurantEntity.getOpenTime(), restaurantEntity.getCloseTime(), LocalTime.now()))
                .build();
    }

    @Transactional
    public WaitingRegisterRespDTO registerWaiting(Long customerId, Long restaurantId, int partySize){
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));

        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        WaitingEntity waitingEntity = WaitingEntity.builder()
                .customer(customerEntity)
                .restaurant(restaurantEntity)
                .status(WaitingStatus.APPLIED)
                .registeredAt(LocalDateTime.now())
                .build();

        WaitingEntity savedWaiting = waitingRepository.save(waitingEntity);

        Integer lastWaitingNumber = waitingRepository
                .findMaxWaitingNumberByRestaurantIdAndRegisteredAt(
                        restaurantId,
                        LocalDate.now()
                )
                .orElse(0);

        savedWaiting.updateWaitingNumber(lastWaitingNumber + 1);

        return WaitingRegisterRespDTO.builder()
                .customerId(customerEntity.getId())
                .customerName(customerEntity.getCustomerName())
                .restaurantId(restaurantEntity.getId())
                .restaurantName(restaurantEntity.getRestaurantName())
                .waitingId(savedWaiting.getId())
                .waitingNumber(savedWaiting.getWaitingNumber())
                .remainingTeamCount(getRemainingTeamCount(savedWaiting.getId()))
                .estimatedWaitingTime("30분")
                .registeredAt(waitingEntity.getRegisteredAt())
                .build();
    }

    @Transactional
    public WaitingCancelRespDTO cancelWaiting(Long restaurantId, Long waitingId, CommonWaitingReqDTO commonWaitingReq){
        WaitingEntity waitingEntity = waitingRepository.findByIdAndStatus(waitingId, WaitingStatus.APPLIED);
        CustomerEntity customerEntity = customerRepository.findById(commonWaitingReq.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        waitingEntity.updateStatus(WaitingStatus.CANCELED);

        return WaitingCancelRespDTO.builder()
                .waitingId(waitingId)
                .restaurantId(restaurantId)
                .restaurantName(restaurantEntity.getRestaurantName())
                .customerId(commonWaitingReq.getCustomerId())
                .customerName(customerEntity.getCustomerName())
                .status(WaitingStatus.CANCELED)
                .canceledAt(LocalDateTime.now())
                .build();
    }

    @Transactional(readOnly = true)
    public WaitingOverviewDTO getWaitingOverview(Long restaurantId){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        int remainingTeam = waitingRepository.countByRestaurantIdAndStatusAndDateRange(restaurantId, WaitingStatus.APPLIED, startOfDay, endOfDay);
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        return WaitingOverviewDTO.builder()
                .restaurantId(restaurantId)
                .restaurantName(restaurantEntity.getRestaurantName())
                .estimatedWaitingTime("1시간")
                .remainingTeamCount(remainingTeam)
                .build();
    }

    @Transactional(readOnly = true)
    public MyWaitingStatusDTO getMyWaiting(Long restaurantId, Long waitingId, CommonWaitingReqDTO commonWaitingReq){
        CustomerEntity customerEntity = customerRepository.findById(commonWaitingReq.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));
        WaitingEntity waitingEntity = waitingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException("웨이팅 등록 상태가 아닙니다."));

        return MyWaitingStatusDTO.builder()
                .customerId(commonWaitingReq.getCustomerId())
                .customerName(customerEntity.getCustomerName())
                .restaurantId(restaurantId)
                .restaurantName(restaurantEntity.getRestaurantName())
                .waitingId(waitingEntity.getId())
                .waitingNumber(waitingEntity.getWaitingNumber())
                .remainingTeamCount(getRemainingTeamCount(waitingEntity.getId()))
                .status(WaitingStatus.APPLIED)
                .estimatedWaitingTime("30분")
                .registeredAt(waitingEntity.getRegisteredAt())
                .build();
    }

    public int getRemainingTeamCount(Long waitingId) {
        WaitingEntity waiting = waitingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException("웨이팅 등록 상태가 아닙니다."));

        return waitingRepository.countByRestaurantIdAndStatusAndRegisteredAtLessThan(
                waiting.getRestaurant().getId(),
                WaitingStatus.APPLIED,
                waiting.getRegisteredAt()
        );
    }

    public boolean isRestaurantOpen(String openTime, String closeTime, LocalTime now) {
        LocalTime open = LocalTime.parse(openTime);
        LocalTime close = LocalTime.parse(closeTime);
        return now.isAfter(open) && now.isBefore(close);
    }
}
