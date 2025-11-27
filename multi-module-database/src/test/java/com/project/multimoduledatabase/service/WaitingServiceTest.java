package com.project.multimoduledatabase.service;

import com.project.multimoduledatabase.Service.WaitingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WaitingServiceTest {

    @InjectMocks
    WaitingService waitingService;

    @Test
    @DisplayName("웨이팅 대기 시간 테스트")
    public void estimatedWaitingTimeTest() {
        assertEquals("약 30분 내 입장 가능", waitingService.estimatedWaitingTime(5));
    }

}