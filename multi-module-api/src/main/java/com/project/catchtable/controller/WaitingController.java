package com.project.catchtable.controller;

import com.project.multimoduledatabase.Service.WaitingService;
import com.project.multimoduledatabase.common.CommonMessage;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/restaurant/{restaurantId}/waiting")
    public ResponseEntity<CommonResp<WaitingRegisterRespDTO>> registerWaiting(@PathVariable("restaurantId") Long restaurantId,
                                                                              @RequestBody WaitingRegisterReqDTO waitingRegisterReq) {

        WaitingRegisterRespDTO waitingRegisterResp = waitingService.registerWaiting(restaurantId
                                                                                    , waitingRegisterReq.getCustomerId()
                                                                                    , waitingRegisterReq.getPartySize());

        CommonResp<WaitingRegisterRespDTO> resp = new CommonResp(
                1000,
                CommonMessage.REGISTER_WAITING_SUCC,
                waitingRegisterResp);

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/restaurant/{restaurantId}/waiting/{waitingId}/cancel")
    public ResponseEntity<CommonResp<WaitingCancelRespDTO>> cancelWaiting(@PathVariable("restaurantId") Long restaurantId,
                                                                           @PathVariable("waitingId") Long waitingId,
                                                                           @RequestBody CommonWaitingReqDTO waitingCancel) {

        WaitingCancelRespDTO waitingCancelResp = waitingService.cancelWaiting(restaurantId, waitingId, waitingCancel);

        CommonResp<WaitingCancelRespDTO> resp = new CommonResp(
                1000,
                CommonMessage.CANCEL_WAITING_SUCC,
                waitingCancelResp);

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/restaurant/{restaurantId}/waiting/status")
    public ResponseEntity<CommonResp<WaitingOverviewDTO>> getRestaurantWaitingOverview(@PathVariable("restaurantId") Long restaurantId) {

        WaitingOverviewDTO waitingOverview = waitingService.getWaitingOverview(restaurantId);

        CommonResp<WaitingOverviewDTO> resp = new CommonResp(
                1000,
                CommonMessage.GET_WAITING_OVERVIEW_SUCC,
                waitingOverview);

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/restaurant/{restaurantId}/my-waiting/{waitingId}")
    public ResponseEntity<CommonResp<MyWaitingStatusDTO>> getMyRestaurantWaitingStatus(@PathVariable("restaurantId") Long restaurantId,
                                                                                     @PathVariable("waitingId") Long waitingId,
                                                                                       @RequestBody CommonWaitingReqDTO commonWaitingReq) {

        MyWaitingStatusDTO myWaitingStatus = waitingService.getMyWaiting(restaurantId, waitingId, commonWaitingReq);

        CommonResp<MyWaitingStatusDTO> resp = new CommonResp(
                1000,
                "Get My Waiting Status OK"
                ,myWaitingStatus);

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
