package com.project.catchtable.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuDTO {

    private Long id;
    private String menuName;
    private int menuPrice;
    private String menuImage;
}
