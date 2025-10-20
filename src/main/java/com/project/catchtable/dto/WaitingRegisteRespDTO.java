package com.project.catchtable.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WaitingRegisteRespDTO {
    private Long customerId;
    private String customerName;
    private Long restaurantId;
    private String restaurantName;
    private Long waitingId;
    private int waitingNumber;
    private int partySize;
    private String status;
    private String estimatedWaitingTime;
    private LocalDateTime registeredAt;
}
