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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyWaitingStatusDTO {

    private Long customerId;
    private String customerName;
    private Long restaurantId;
    private String restaurantName;
    private Long waitingId;
    private int waitingNumber;
    private int remainingTeamCount;
    private WaitingStatus status;
    private String estimatedWaitingTime;
    private LocalDate registeredDate;
    private LocalTime registeredTime;
}
