package com.project.multimoduledatabase.dto;

import com.project.multimoduledatabase.enums.WaitingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingCancelRespDTO {

    private Long waitingId;
    private Long restaurantId;
    private String restaurantName;
    private Long customerId;
    private String customerName;
    private WaitingStatus status;
    private LocalDateTime canceledAt;
}
