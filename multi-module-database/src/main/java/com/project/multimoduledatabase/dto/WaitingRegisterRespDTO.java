package com.project.multimoduledatabase.dto;

import com.project.multimoduledatabase.enums.WaitingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDate registeredDate;
    private LocalTime registeredTime;
}
