package com.project.multimoduledatabase.dto;

import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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

    public static WaitingCancelRespDTO from(WaitingEntity waiting) {
        return new WaitingCancelRespDTO(
                waiting.getId(),
                waiting.getRestaurant().getId(),
                waiting.getRestaurant().getName(),
                waiting.getCustomer().getId(),
                waiting.getCustomer().getName(),
                waiting.getStatus(),
                LocalDateTime.now()
        );
    }
}
