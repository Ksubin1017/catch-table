package com.project.multimoduledatabase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.catchtable.avro.WaitingCall;
import com.project.multimoduledatabase.dto.SlackSendDTO;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlackService {
    private final KafkaTemplate<String, WaitingCall> kafkaTemplate;
    public void sendMessage(RestaurantEntity restaurant, int waitingNumber, int nextWaitingNumber, String webhookUrl) {
        WaitingCall waitingCall = WaitingCall.newBuilder()
                .setRestaurantName(restaurant.getName())
                .setWaitingNumber(waitingNumber)
                .setNextWaitingNumber(nextWaitingNumber)
                .setWebhookUrl(webhookUrl)
                .build();

        try {
            kafkaTemplate.send("waiting-call-slack", waitingCall);
            log.info("Kafka message sent successfully: {}", waitingCall);
        } catch (Exception e) {
            log.error("Failed to send Kafka message: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 메시지 전송 실패", e);
        }
    }

    private String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 직렬화 실패");
        }
    }
}
