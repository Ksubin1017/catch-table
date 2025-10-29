package com.project.multimoduledatabase.dto;

import com.project.multimoduledatabase.enums.WaitingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WaitingCancelRespDTO {

    private Long waitingId;
    private Long restaurantId;
    private String restaurantName;
    private Long customerId;
    private String customerName;
    private WaitingStatus status;
    private LocalDateTime canceledAt;
}
