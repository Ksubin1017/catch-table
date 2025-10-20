package com.project.catchtable.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaitingOverviewDTO {

    private Long restaurantId;
    private String restaurantName;
    private String estimatedWaitingTime;
    private int remainingTeamCount;
}
