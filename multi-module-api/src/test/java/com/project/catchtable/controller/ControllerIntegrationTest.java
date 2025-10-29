package com.project.catchtable.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.multimoduledatabase.dto.CommonWaitingReqDTO;
import com.project.multimoduledatabase.dto.WaitingRegisterReqDTO;
import com.project.multimoduledatabase.enums.WaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WaitingController.class)
public class ControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("식당 카테고리 목록 조회")
    void testGetRestaurantCategory() throws Exception {
        // given
        String category = "korean";

        // when & then
        mockMvc.perform(get("/restaurant/{category}", category)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Get Restaurant Category List OK"))
                .andExpect(jsonPath("$.data.restaurantItems[0].restaurantId").value(1))
                .andExpect(jsonPath("$.data.restaurantItems[0].restaurantName").value("김밥천국"));
    }

    @Test
    @DisplayName("식당 상세 조회")
    void testGetRestaurant() throws Exception {
        // given
        Long restaurantId = 1L;

        // when & then
        mockMvc.perform(get("/restaurant/{restaurantId}/detail", restaurantId)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Get Restaurant Detail OK"))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId))
                .andExpect(jsonPath("$.data.restaurantName").value("김밥천국"))
                .andExpect(jsonPath("$.data.restaurantImage").value("https://flobby.s3.ap-northeast-2.amazonaws.com/default_profile.png"))
                .andExpect(jsonPath("$.data.restaurantAddr").value("서울특별시 강남구 테헤란로 17"))
                .andExpect(jsonPath("$.data.menuList[0].id").value(1))
                .andExpect(jsonPath("$.data.menuList[0].menuName").value("라면"))
                .andExpect(jsonPath("$.data.menuList[0].menuPrice").value(3000))
                .andExpect(jsonPath("$.data.menuList[0].menuImage").value("https://aaa.com"))
                .andExpect(jsonPath("$.data.isOpen").value(true))
                .andExpect(jsonPath("$.data.businessHours.open").value("10:00"))
                .andExpect(jsonPath("$.data.businessHours.close").value("22:00"));
    }

    @Test
    @DisplayName("웨이팅 등록")
    void testRegisterWaiting() throws Exception {
        // given
        Long restaurantId = 1L;
        WaitingRegisterReqDTO waitingRegisterReqDTO = new WaitingRegisterReqDTO();
        waitingRegisterReqDTO.setCustomerId(1L);
        waitingRegisterReqDTO.setPartySize(2);

        String requestBody = objectMapper.writeValueAsString(waitingRegisterReqDTO);
        // when && then
        mockMvc.perform(post("/restaurant/{restaurantId}/waiting", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Register Waiting OK"))
                .andExpect(jsonPath("$.data.customerId").value(1))
                .andExpect(jsonPath("$.data.customerName").value("김수빈"))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId))
                .andExpect(jsonPath("$.data.restaurantName").value("츠케루"))
                .andExpect(jsonPath("$.data.waitingId").value(100L))
                .andExpect(jsonPath("$.data.waitingNumber").value(17))
                .andExpect(jsonPath("$.data.partySize").value(2))
                .andExpect(jsonPath("$.data.status").value(WaitingStatus.APPLIED.name()))
                .andExpect(jsonPath("$.data.estimatedWaitingTime").value("1시간 10분"));
    }

    @Test
    @DisplayName("웨이팅 취소")
    void testCancelWaiting() throws Exception {
        Long restaurantId = 1L;
        Long waitingId = 100L;
        CommonWaitingReqDTO commonWaitingReqDTO = new CommonWaitingReqDTO();
        commonWaitingReqDTO.setCustomerId(1L);

        String requestBody = objectMapper.writeValueAsString(commonWaitingReqDTO);

        mockMvc.perform(post("/restaurant/{restaurantId}/waiting/{waitingId}/cancel", restaurantId, waitingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Cancel Waiting OK"))
                .andExpect(jsonPath("$.data.waitingId").value(100L))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId))
                .andExpect(jsonPath("$.data.restaurantName").value("츠케루"))
                .andExpect(jsonPath("$.data.customerId").value(1L))
                .andExpect(jsonPath("$.data.customerName").value("김수빈"))
                .andExpect(jsonPath("$.data.status").value(WaitingStatus.CANCELED.name()));
    }

    @Test
    @DisplayName("웨이팅 현황 조회(비등록)")
    void testRestaurantWaitingOverview() throws Exception {
        Long restaurantId = 1L;

        mockMvc.perform(post("/restaurant/{restaurantId}/waiting/status", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Get Waiting Status OK"))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId))
                .andExpect(jsonPath("$.data.restaurantName").value("초이식당"))
                .andExpect(jsonPath("$.data.estimatedWaitingTime").value("30분"))
                .andExpect(jsonPath("$.data.remainingTeamCount").value(7));
    }

    @Test
    @DisplayName("웨이팅 현황 조회(등록)")
    void testMyRestaurantWaitingStatus() throws Exception {
        Long restaurantId = 1L;
        Long waitingId = 100L;
        CommonWaitingReqDTO commonWaitingReqDTO = new CommonWaitingReqDTO();
        commonWaitingReqDTO.setCustomerId(1L);

        String requestBody = objectMapper.writeValueAsString(commonWaitingReqDTO);

        mockMvc.perform(post("/restaurant/{restaurantId}/waiting/{waitingId}/my", restaurantId, waitingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Get My Waiting Status OK"))
                .andExpect(jsonPath("$.data.customerId").value(1L))
                .andExpect(jsonPath("$.data.customerName").value("홍길동"))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId))
                .andExpect(jsonPath("$.data.restaurantName").value("초이식당"))
                .andExpect(jsonPath("$.data.waitingId").value(waitingId))
                .andExpect(jsonPath("$.data.waitingNumber").value(17))
                .andExpect(jsonPath("$.data.remainingTeamCount").value(7))
                .andExpect(jsonPath("$.data.status").value(WaitingStatus.APPLIED.name()))
                .andExpect(jsonPath("$.data.estimatedWaitingTime").value("30분"));
    }
}
