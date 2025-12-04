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

        CommonResp<RestaurantWaitingStatusOwnerDTO> resp = new CommonResp<>(
                1000,
                CommonMessage.GET_RESTAURANT_STATUS_OWNER_SUCC,
                restaurantOwnerService.getRestaurantWaitingStatusOwner(restaurantId)
        );

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/waiting/call")
    public ResponseEntity<CommonResp<Object>> callWaiting(@RequestBody WaitingCallReqDTO waitingCallReqDTO) {
        restaurantOwnerService.callWaiting(waitingCallReqDTO);

        CommonResp<Object> resp = new CommonResp<>(
                1000,
                CommonMessage.CALL_WAITING_SUCC,
                null
        );

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
