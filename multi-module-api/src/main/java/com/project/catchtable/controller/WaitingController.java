package com.project.catchtable.controller;

import com.project.multimoduledatabase.Service.WaitingService;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.enums.WaitingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/restaurant/{restaurantId}/waiting")
    public ResponseEntity<CommonResp<WaitingRegisterRespDTO>> registerWaiting(@PathVariable("restaurantId") Long restaurantId,
                                                                              @RequestBody WaitingRegisterReqDTO waitingRegisterReq) {

        WaitingRegisterRespDTO waitingRegisterResp = WaitingRegisterRespDTO.builder()
                .customerId(waitingRegisterReq.getCustomerId())
                .customerName("김수빈")
                .restaurantId(restaurantId)
                .restaurantName("츠케루")
                .waitingId(100L)
                .waitingNumber(17)
                .partySize(2)
                .status(WaitingStatus.APPLIED)
                .estimatedWaitingTime("1시간 10분")
                .registeredAt(LocalDateTime.now())
                .build();


        CommonResp<WaitingRegisterRespDTO> resp = CommonResp.<WaitingRegisterRespDTO>builder()
                .code(1000)
                .message("Register Waiting OK")
                .data(waitingRegisterResp)
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
                .status(WaitingStatus.CANCELED)
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
                                                                                     @PathVariable("waitingId") Long waitingId,
                                                                                       @RequestBody CommonWaitingReqDTO commonWaitingReq) {

        MyWaitingStatusDTO myWaitingStatus = MyWaitingStatusDTO.builder()
                .customerId(1L)
                .customerName("홍길동")
                .restaurantId(restaurantId)
                .restaurantName("초이식당")
                .waitingId(waitingId)
                .waitingNumber(17)
                .remainingTeamCount(7)
                .status(WaitingStatus.APPLIED)
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
