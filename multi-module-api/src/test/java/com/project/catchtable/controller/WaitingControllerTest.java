package com.project.catchtable.controller;

import com.project.multimoduledatabase.Service.WaitingService;
import com.project.multimoduledatabase.common.CommonCode;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.entity.CustomerEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import com.project.multimoduledatabase.repository.CustomerRepository;
import com.project.multimoduledatabase.repository.WaitingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WaitingControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private WaitingService waitingService;
    @Autowired
    private WaitingController waitingController;

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

        WaitingRegisterReqDTO waitingRegisterReqDTO = new WaitingRegisterReqDTO(
                customerId,
                2);

        // When
        ResponseEntity<CommonResp<WaitingRegisterRespDTO>> response = restTemplate.exchange(
                "/restaurant/{id}/waiting",
                HttpMethod.POST,
                new HttpEntity<>(waitingRegisterReqDTO),
                new ParameterizedTypeReference<CommonResp<WaitingRegisterRespDTO>>() {
                },
                id
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getBody().getData().getCustomerName()).isEqualTo("박영희");
        assertThat(response.getBody().getData().getRemainingTeamCount()).isEqualTo(2);
        assertThat(response.getBody().getData().getWaitingNumber()).isEqualTo(3);
        assertThat(response.getBody().getData().getStatus()).isEqualTo(WaitingStatus.APPLIED);
    }

    @Test
    @DisplayName("웨이팅 중복 등록")
    void duplicateRegisterWaiting() throws Exception {

        // Given
        Long id = 1L;
        Long customerId = customerRepository.findByName("홍길동").getId();

        WaitingRegisterReqDTO waitingRegisterReqDTO = new WaitingRegisterReqDTO(
                customerId,
                2);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            waitingController.registerWaiting(id, waitingRegisterReqDTO);
        });

    }

    @Test
    @DisplayName("웨이팅 취소")
    void cancelWaiting() throws Exception {
        // Given
        Long id = 1L;
        Long customerId = customerRepository.findByName("홍길동").getId();
        Long waitingId = waitingRepository.findByCustomer_Id(customerId).getId();
        CommonWaitingReqDTO waitingReqDTO = new CommonWaitingReqDTO(
                customerId);

        // When
        ResponseEntity<CommonResp<WaitingCancelRespDTO>> response = restTemplate.exchange(
                "/restaurant/{id}/waiting/{waitingId}/cancel",
                HttpMethod.POST,
                new HttpEntity<>(waitingReqDTO),
                new ParameterizedTypeReference<CommonResp<WaitingCancelRespDTO>>() {
                },
                id,
                waitingId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getBody().getData().getStatus()).isEqualTo(WaitingStatus.CANCELED);
    }

    @Test
    @DisplayName("웨이팅 오버뷰")
    void waitingOverview() throws Exception {
        // Given
        Long id = 1L;

        // When
        ResponseEntity<CommonResp<WaitingOverviewDTO>> response = restTemplate.exchange(
                "/restaurant/{restaurantId}/waiting/status",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CommonResp<WaitingOverviewDTO>>() {
                },
                id
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getBody().getData().getRemainingTeamCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("내 웨이팅 현황")
    void myWaitingStatus() throws Exception {
        // Given
        Long id = 1L;
        Long customerId = customerRepository.findByName("김철수").getId();
        Long waitingId = waitingRepository.findByCustomer_Id(customerId).getId();
        CommonWaitingReqDTO waitingReqDTO = new CommonWaitingReqDTO(
                customerId);

        // When
        ResponseEntity<CommonResp<MyWaitingStatusDTO>> response = restTemplate.exchange(
                "/restaurant/{restaurantId}/my-waiting/{waitingId}",
                HttpMethod.POST,
                new HttpEntity<>(waitingReqDTO),
                new ParameterizedTypeReference<CommonResp<MyWaitingStatusDTO>>() {
                },
                id,
                waitingId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getBody().getData().getStatus()).isEqualTo(WaitingStatus.APPLIED);
        assertThat(response.getBody().getData().getWaitingNumber()).isEqualTo(2);
        assertThat(response.getBody().getData().getRemainingTeamCount()).isEqualTo(1);
    }
}
