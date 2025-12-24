package com.project.multimoduledatabase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackSendDTO {
    private String restaurantName;
    private int waitingNumber;
    private int nextWaitingNumber;
    private String webhookUrl;
}
