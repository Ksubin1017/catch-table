package com.project.catchtable.controller;

import com.project.multimoduledatabase.Service.RestaurantService;
import com.project.multimoduledatabase.common.CommonMessage;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/restaurant/{category}")
    public ResponseEntity<CommonResp<List<RestaurantListItemDTO>>> getRestaurantCategory(@PathVariable("category") RestaurantCategory category) {

        List<RestaurantListItemDTO> restaurantList = restaurantService.getRestaurantList(category);

        CommonResp<List<RestaurantListItemDTO>> resp = CommonResp.<List<RestaurantListItemDTO>>builder()
                .code(1000)
                .message(CommonMessage.GET_RESTAURANT_BY_CATEGORY_SUCC)
                .data(restaurantList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/restaurant/{restaurantId}/detail")
    public ResponseEntity<CommonResp<RestaurantDetailDTO>> getRestaurantDetail(@PathVariable("restaurantId") Long restaurantId) {

        RestaurantDetailDTO restaurantDetailDTO = restaurantService.getRestaurantDetail(restaurantId);

        CommonResp<RestaurantDetailDTO> resp = CommonResp.<RestaurantDetailDTO>builder()
                .code(1000)
                .message(CommonMessage.GET_RESTAURANT_DETAIL_SUCC)
                .data(restaurantDetailDTO)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
