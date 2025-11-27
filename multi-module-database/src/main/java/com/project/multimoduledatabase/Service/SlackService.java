package com.project.multimoduledatabase.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import com.project.multimoduledatabase.repository.WaitingRepository;
import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlackService {
    @Value(value = "${slack_token}")
    String slackToken;

    ObjectMapper mapper = new ObjectMapper();

    public String getSlackUserId(String email) throws JsonProcessingException {
        System.out.println("encode email: " +  URLEncoder.encode(email, StandardCharsets.UTF_8));
        String url = "https://slack.com/api/users.lookupByEmail?email=" + email;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(slackToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        String resp = response.getBody();
        JsonNode body = mapper.readTree(response.getBody());
        System.out.println("Slack Response: " + resp);

        if (body.get("ok").asBoolean()) {
            System.out.println("Slack Response: " + resp);
            return body.get("user").get("id").textValue();
        }
        else
            return null;
    }

    public void sendMessage(RestaurantEntity restaurant, int waitingNumber, int nextWaitingNumber, String webhookUrl) throws IOException {
        Slack slack = Slack.getInstance();

        String payload = "{\"text\": \"" + "[" + restaurant.getName() + "]" + "\n" + waitingNumber + "번 고객님, 입장해주세요." + "\"}";
        WebhookResponse response = slack.send(webhookUrl, payload);

        if(nextWaitingNumber != 0) {
            String payload2 = "{\"text\": \""
                    + "[" + restaurant.getName() + "]\\n"
                    + nextWaitingNumber + "번 고객님, 다음 차례 입장입니다. 앞에서 대기해주세요.\"}";
            response = slack.send(webhookUrl, payload2);
        }
    }
}
