package com.project.multimoduledatabase.dto;

import com.project.multimoduledatabase.enums.WaitingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WaitingRegisterRespDTO {
    private Long customerId;
    private String customerName;
    private Long restaurantId;
    private String restaurantName;
    private Long waitingId;
    private int waitingNumber;
    private int partySize;
    private WaitingStatus status;
    private int remainingTeamCount;
    private String estimatedWaitingTime;
    private LocalDateTime registeredAt;
}
