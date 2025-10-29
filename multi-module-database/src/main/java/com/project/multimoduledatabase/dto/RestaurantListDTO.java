package com.project.multimoduledatabase.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestaurantListDTO {

    private List<restaurantItemListDTO> restaurantItems;
}
