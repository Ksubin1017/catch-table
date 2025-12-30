package com.project.catchtable.controller;

import com.project.multimoduledatabase.service.RestaurantOwnerService;
import com.project.multimoduledatabase.common.CommonMessage;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.RestaurantWaitingStatusOwnerDTO;
import com.project.multimoduledatabase.dto.WaitingCallReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class RestaurantOwnerController {

    private final RestaurantOwnerService restaurantOwnerService;
    
    @GetMapping("/restaurant/{restaurantId}/waiting/status/owner")
    public CompletableFuture<ResponseEntity<CommonResp<RestaurantWaitingStatusOwnerDTO>>> getRestaurantStatusOwner(
            @PathVariable(name = "restaurantId") Long restaurantId) {

        return restaurantOwnerService.getRestaurantWaitingStatusOwner(restaurantId)
                .thenApply(dto -> {
                    CommonResp<RestaurantWaitingStatusOwnerDTO> resp = new CommonResp<>(
                            1000,
                            CommonMessage.GET_RESTAURANT_STATUS_OWNER_SUCC,
                            dto
                    );
                    return ResponseEntity.status(HttpStatus.OK).body(resp);
                });
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
