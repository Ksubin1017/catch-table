package com.project.catchtable.controller;

import com.project.multimoduledatabase.Service.RestaurantOwnerService;
import com.project.multimoduledatabase.Service.WaitingService;
import com.project.multimoduledatabase.common.CommonMessage;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.RestaurantWaitingStatusOwnerDTO;
import com.project.multimoduledatabase.dto.WaitingCallReqDTO;
import com.project.multimoduledatabase.dto.WaitingCancelRespDTO;
import com.project.multimoduledatabase.entity.WaitingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RestaurantOwnerController {

    private final RestaurantOwnerService restaurantOwnerService;

    @GetMapping("/restaurant/{restaurantId}/waiting/status/owner")
    public ResponseEntity<CommonResp<RestaurantWaitingStatusOwnerDTO>> getRestaurantStatusOwner(@PathVariable(name = "restaurantId") Long restaurantId) {

        CommonResp<RestaurantWaitingStatusOwnerDTO> resp = CommonResp.<RestaurantWaitingStatusOwnerDTO>builder()
                .code(1000)
                .message(CommonMessage.GET_RESTAURANT_STATUS_OWNER_SUCC)
                .data(restaurantOwnerService.getRestaurantWaitingStatusOwner(restaurantId))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/waiting/call")
    public ResponseEntity<CommonResp<Object>> callWaiting(@RequestBody WaitingCallReqDTO waitingCallReqDTO) {
        try {
            restaurantOwnerService.callWaiting(waitingCallReqDTO);

            CommonResp<Object> resp = CommonResp.builder()
                    .code(1000)
                    .message(CommonMessage.CALL_WAITING_SUCC)
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (Exception e) {
            CommonResp<Object> resp = CommonResp.builder()
                    .code(1001)
                    .message(CommonMessage.CALL_WAITING_FAIL)
                    .data(null)
                    .build();
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }
    }
}
