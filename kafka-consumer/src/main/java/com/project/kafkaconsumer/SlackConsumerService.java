package com.project.kafkaconsumer;

import com.project.catchtable.avro.WaitingCall;
import com.slack.api.Slack;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SlackConsumerService {
    @KafkaListener(
            topics = "waiting-call-slack",
            groupId = "slack-send-group"
    )
    public void comsume(WaitingCall waitingCall) {
        try {
            Slack slack = Slack.getInstance();

            String payload = "{\"text\": \"" + "[" + waitingCall.getRestaurantName() + "]"
                    + "\n" + waitingCall.getWaitingNumber() + "번 고객님, 입장해주세요." + "\"}";
            slack.send(waitingCall.getWebhookUrl(), payload);

            if (waitingCall.getNextWaitingNumber() != 0) {
                String payload2 = "{\"text\": \""
                        + "[" + waitingCall.getRestaurantName() + "]\\n"
                        + waitingCall.getNextWaitingNumber() + "번 고객님, 다음 차례 입장입니다. 앞에서 대기해주세요.\"}";
                slack.send(waitingCall.getWebhookUrl(), payload2);
            }

        } catch (IOException e) {
            throw new RuntimeException("Slack 메시지 전송 실패: " + e.getMessage(), e);
        }
    }

}
