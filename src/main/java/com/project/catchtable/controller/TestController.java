package com.project.catchtable.controller;

import com.project.catchtable.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @GetMapping("/restaurant/{category}")
    public ResponseEntity<CommonResp<RestaurantListDTO>> getRestaurantCategory(@PathVariable("category") String category) {
        List<RestaurantListItemDTO> restaurantItemListDTO = new ArrayList<>();

        RestaurantListItemDTO restaurant = RestaurantListItemDTO.builder()
                .restaurantId(1L)
                .restaurantName("김밥천국")
                .restaurantAddr("서울특별시 강남구 테헤란로 17")
                .restaurantImage("https://flobby.s3.ap-northeast-2.amazonaws.com/default_profile.png")
                .price(5000)
                .isOpen(true)
                .businessHours(BusinessHoursDTO.builder()
                        .open("10:00")
                        .close("22:00")
                        .build())
                .build();

        restaurantItemListDTO.add(restaurant);

        RestaurantListDTO restaurantList = RestaurantListDTO.builder()
                .restaurantItems(restaurantItemListDTO)
                .build();

        CommonResp<RestaurantListDTO> resp = CommonResp.<RestaurantListDTO>builder()
                .code(1000)
                .message("Get Restaurant Category List OK")
                .data(restaurantList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/restaurant/{restaurantId}/detail")
    public ResponseEntity<CommonResp<RestaurantDetailDTO>> getRestaurantDetail(@PathVariable("restaurantId") Long restaurantId) {

        List<MenuDTO> menuList = new ArrayList<>();
        MenuDTO menu = MenuDTO.builder()
                .id(1L)
                .menuName("라면")
                .menuPrice(3000)
                .menuImage("https://aaa.com")
                .build();

        menuList.add(menu);

        RestaurantDetailDTO restaurantDetail = RestaurantDetailDTO.builder()
                .restaurantId(1L)
                .restaurantName("김밥천국")
                .restaurantImage("https://flobby.s3.ap-northeast-2.amazonaws.com/default_profile.png")
                .restaurantAddr("서울특별시 강남구 테헤란로 17")
                .menuList(menuList)
                .isOpen(true)
                .businessHours(BusinessHoursDTO.builder()
                        .open("10:00")
                        .close("22:00")
                        .build())
                .build();

        CommonResp<RestaurantDetailDTO> resp = CommonResp.<RestaurantDetailDTO>builder()
                .code(1000)
                .message("Get Restaurant Detail OK")
                .data(restaurantDetail)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/restaurant/{restaurantId}/waiting")
    public ResponseEntity<CommonResp<WaitingRegisteRespDTO>> registerWaiting(@PathVariable("restaurantId") Long restaurantId,
                                                                         @RequestBody WaitingRegisterReqDTO waitingRegisterReq) {

        WaitingRegisteRespDTO waitingRegisteResp = WaitingRegisteRespDTO.builder()
                .customerId(waitingRegisterReq.getCustomerId())
                .customerName("김수빈")
                .restaurantId(restaurantId)
                .restaurantName("츠케루")
                .waitingId(100L)
                .waitingNumber(17)
                .partySize(2)
                .status("applied")
                .estimatedWaitingTime("1시간 10분")
                .registeredAt(LocalDateTime.now())
                .build();


        CommonResp<WaitingRegisteRespDTO> resp = CommonResp.<WaitingRegisteRespDTO>builder()
                .code(1000)
                .message("Register Waiting OK")
                .data(waitingRegisteResp)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/restaurant/{restaurantId}/waiting/{waitingId}/cancel")
    public ResponseEntity<CommonResp<WaitingCancelRespDTO>> cancelWaiting(@PathVariable("restaurantId") Long restaurantId,
                                                                           @PathVariable("waitingId") Long waitingId,
                                                                           @RequestBody CommonWaitingReqDTO waitingCancel) {

        WaitingCancelRespDTO waitingCancelResp = WaitingCancelRespDTO.builder()
                .waitingId(100L)
                .restaurantId(restaurantId)
                .restaurantName("츠케루")
                .customerId(waitingCancel.getCustomerId())
                .customerName("김수빈")
                .status("canceled")
                .canceledAt(LocalDateTime.now())
                .build();

        CommonResp<WaitingCancelRespDTO> resp = CommonResp.<WaitingCancelRespDTO>builder()
                .code(1000)
                .message("Cancel Waiting OK")
                .data(waitingCancelResp)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/restaurant/{restaurantId}/waiting/status")
    public ResponseEntity<CommonResp<WaitingOverviewDTO>> getRestaurantWaitingOverview(@PathVariable("restaurantId") Long restaurantId) {

        WaitingOverviewDTO waitingOverview = WaitingOverviewDTO.builder()
                .restaurantId(restaurantId)
                .restaurantName("초이식당")
                .estimatedWaitingTime("30분")
                .remainingTeamCount(7)
                .build();

        CommonResp<WaitingOverviewDTO> resp = CommonResp.<WaitingOverviewDTO>builder()
                .code(1000)
                .message("Get Waiting Status OK")
                .data(waitingOverview)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/restaurant/{restaurantId}/waiting/{waitingId}/my")
    public ResponseEntity<CommonResp<MyWaitingStatusDTO>> getMyRestaurantWaitingStatus(@PathVariable("restaurantId") Long restaurantId,
                                                                                     @PathVariable("waitingId") Long waitingId) {

        MyWaitingStatusDTO myWaitingStatus = MyWaitingStatusDTO.builder()
                .customerId(1L)
                .customerName("홍길동")
                .restaurantId(restaurantId)
                .restaurantName("초이식당")
                .waitingId(waitingId)
                .waitingNumber(17)
                .remainingTeamCount(7)
                .status("applied")
                .estimatedWaitingTime("30분")
                .registeredAt(LocalDateTime.now())
                .build();

        CommonResp<MyWaitingStatusDTO> resp = CommonResp.<MyWaitingStatusDTO>builder()
                .code(1000)
                .message("Get My Waiting Status OK")
                .data(myWaitingStatus)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
