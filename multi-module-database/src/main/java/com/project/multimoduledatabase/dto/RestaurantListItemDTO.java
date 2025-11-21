package com.project.multimoduledatabase.dto;

import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListItemDTO {
    private Long id;
    private String name;
    private String addr;
    private String image;
    private RestaurantCategory category;
    private int price;
    private BusinessHoursDTO businessHours;
    private Boolean isOpen;

}
