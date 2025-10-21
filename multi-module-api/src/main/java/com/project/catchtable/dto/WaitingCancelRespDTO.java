package com.project.catchtable.dto;

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
    private String status;
    private LocalDateTime canceledAt;
}
