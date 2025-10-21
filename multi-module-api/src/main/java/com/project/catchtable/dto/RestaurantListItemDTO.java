package com.project.catchtable.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantListItemDTO {
    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddr;
    private String restaurantImage;
    private int price;
    private Boolean isOpen;
    private BusinessHoursDTO businessHours;
}
