package com.project.multimoduledatabase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestaurantDetailDTO {

    private Long restaurantId;
    private String restaurantImage;
    private String restaurantName;
    private String restaurantAddr;
    private List<MenuDTO> menuList;
    @JsonProperty("isOpen")
    private boolean isOpen;
    private BusinessHoursDTO businessHours;
}
