package com.project.multimoduledatabase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitingOverviewDTO {

    private Long restaurantId;
    private String restaurantName;
    private String estimatedWaitingTime;
    private int remainingTeamCount;
}
