package com.project.catchtable.dto;

import lombok.Data;

@Data
public class WaitingRegisterReqDTO {
    private Long customerId;
    private int partySize;
}
