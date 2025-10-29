package com.project.multimoduledatabase.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class restaurantItemListDTO {
    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddr;
    private String restaurantImage;
    private String category;
    private int price;
    private Boolean isOpen;
    private BusinessHoursDTO businessHours;
}
