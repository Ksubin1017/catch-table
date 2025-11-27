package com.project.multimoduledatabase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitingOverviewOwnerDTO {
    private Long customerId;
    private String customerName;
    private int waitingNumber;
    private LocalDate registeredDate;
    private LocalTime registeredTime;
}
