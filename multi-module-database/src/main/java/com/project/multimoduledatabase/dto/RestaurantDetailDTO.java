package com.project.multimoduledatabase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDetailDTO {

    private Long id;
    private String image;
    private String name;
    private String addr;
    private List<MenuDTO> menuList;
    private Boolean isOpen;
    private BusinessHoursDTO businessHours;
}
