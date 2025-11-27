package com.project.catchtable.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.multimoduledatabase.Service.WaitingService;
import com.project.multimoduledatabase.common.CommonCode;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.entity.CustomerEntity;
import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import com.project.multimoduledatabase.repository.CustomerRepository;
import com.project.multimoduledatabase.repository.WaitingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class WaitingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired CustomerRepository customerRepository;
    @Autowired WaitingRepository waitingRepository;
    @Autowired WaitingService waitingService;

    @BeforeEach
    void setUp() {
        waitingRepository.deleteAll();
        customerRepository.deleteAll();

        CustomerEntity customer1 = CustomerEntity.builder()
                .name("홍길동")
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .name("김철수")
                .build();

        CustomerEntity customer3 = CustomerEntity.builder()
                .name("박영희")
                .build();

        Long customerId1 = customerRepository.save(customer1).getId();
        Long customerId2 = customerRepository.save(customer2).getId();
        customerRepository.save(customer3);

        waitingService.registerWaiting(1L, customerId1, 2);
        waitingService.registerWaiting(1L, customerId2, 4);
    }

    @Test
    @DisplayName("웨이팅 등록")
    void registerWaiting() throws Exception {

        // Given
        Long id = 1L;
        Long customerId = customerRepository.findByName("박영희").getId();

        WaitingRegisterReqDTO waitingRegisterReqDTO = WaitingRegisterReqDTO.builder()
                .customerId(customerId)
                .partySize(2)
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/restaurant/{restaurantId}/waiting", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(waitingRegisterReqDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CommonResp<WaitingRegisterRespDTO> response =
                objectMapper.readValue(body, new TypeReference<CommonResp<WaitingRegisterRespDTO>>() {
                });

        assertThat(response.getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getCustomerName()).isEqualTo("박영희");
        assertThat(response.getData().getRemainingTeamCount()).isEqualTo(2);
        assertThat(response.getData().getWaitingNumber()).isEqualTo(3);
        assertThat(response.getData().getStatus()).isEqualTo(WaitingStatus.APPLIED);
    }

    @Test
    @DisplayName("웨이팅 취소")
    void cancelWaiting() throws Exception {
        // Given
        Long id = 1L;
        Long customerId = customerRepository.findByName("홍길동").getId();
        Long waitingId = waitingRepository.findByCustomer_Id(customerId).getId();
        CommonWaitingReqDTO waitingReqDTO = CommonWaitingReqDTO.builder()
                .customerId(customerId)
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/restaurant/{restaurantId}/waiting/{waitingId}/cancel", id, waitingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(waitingReqDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CommonResp<WaitingCancelRespDTO> response =
                objectMapper.readValue(body, new TypeReference<CommonResp<WaitingCancelRespDTO>>() {
                });
        assertThat(response.getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getStatus()).isEqualTo(WaitingStatus.CANCELED);
    }

    @Test
    @DisplayName("웨이팅 오버뷰")
    void waitingOverview() throws Exception {
        // Given
        Long id = 1L;

        // When
        MvcResult result = mockMvc.perform(get("/restaurant/{restaurantId}/waiting/status", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CommonResp<WaitingOverviewDTO> response =
                objectMapper.readValue(body, new TypeReference<CommonResp<WaitingOverviewDTO>>() {
                });
        assertThat(response.getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getRemainingTeamCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("내 웨이팅 현황")
    void myWaitingStatus() throws Exception {
        // Given
        Long id = 1L;
        Long customerId = customerRepository.findByName("김철수").getId();
        Long waitingId = waitingRepository.findByCustomer_Id(customerId).getId();
        CommonWaitingReqDTO waitingReqDTO = CommonWaitingReqDTO.builder()
                .customerId(customerId)
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/restaurant/{restaurantId}/my-waiting/{waitingId}", id, waitingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(waitingReqDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CommonResp<MyWaitingStatusDTO> response =
                objectMapper.readValue(body, new TypeReference<CommonResp<MyWaitingStatusDTO>>() {
                });

        assertThat(response.getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getStatus()).isEqualTo(WaitingStatus.APPLIED);
        assertThat(response.getData().getWaitingNumber()).isEqualTo(2);
        assertThat(response.getData().getRemainingTeamCount()).isEqualTo(1);
    }
}
