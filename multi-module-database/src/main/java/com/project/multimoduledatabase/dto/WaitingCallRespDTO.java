package com.project.multimoduledatabase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitingCallRespDTO {
    private Long restaurantId;
    private String restaurantName;
    private Long waitingId;
    private int waitingNumber;
}
