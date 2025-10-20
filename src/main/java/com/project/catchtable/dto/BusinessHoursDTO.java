package com.project.catchtable.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessHoursDTO {

    private String open;
    private String close;
}
