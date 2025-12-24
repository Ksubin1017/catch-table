package com.project.multimoduledatabase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final KafkaTemplate<String, String> kafkaTemplate;
    public void sendMessage(RestaurantEntity restaurant, int waitingNumber, int nextWaitingNumber, String webhookUrl) {
        SlackSendDTO slackSend = new SlackSendDTO(restaurant.getName(), waitingNumber, nextWaitingNumber, webhookUrl);

        this.kafkaTemplate.send("waiting-call-slack", toJsonString(slackSend));

//        try {
//            Slack slack = Slack.getInstance();
//
//            String payload = "{\"text\": \"" + "[" + restaurant.getName() + "]"
//                    + "\n" + waitingNumber + "번 고객님, 입장해주세요." + "\"}";
//            slack.send(webhookUrl, payload);
//
//            if (nextWaitingNumber != 0) {
//                String payload2 = "{\"text\": \""
//                        + "[" + restaurant.getName() + "]\\n"
//                        + nextWaitingNumber + "번 고객님, 다음 차례 입장입니다. 앞에서 대기해주세요.\"}";
//                slack.send(webhookUrl, payload2);
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException("Slack 메시지 전송 실패: " + e.getMessage(), e);
//        }
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
