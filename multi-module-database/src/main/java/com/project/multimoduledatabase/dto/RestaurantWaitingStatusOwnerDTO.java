package com.project.multimoduledatabase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantWaitingStatusOwnerDTO {

    public Long restaurantId;
    public String restaurantName;
    public int totalWaitingTeamCount;
    public int currentWaitingTeamCount;
    List<WaitingOverviewOwnerDTO> waitingOverviews;
}
