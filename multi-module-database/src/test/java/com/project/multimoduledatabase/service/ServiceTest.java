package com.project.multimoduledatabase.service;

import com.project.multimoduledatabase.Service.WaitingService;
import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.entity.CustomerEntity;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.entity.WaitingEntity;
import com.project.multimoduledatabase.enums.WaitingStatus;
import com.project.multimoduledatabase.repository.CustomerRepository;
import com.project.multimoduledatabase.repository.RestaurantRepository;
import com.project.multimoduledatabase.repository.WaitingRepository;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock private WaitingRepository waitingRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks private WaitingService waitingService;

    private Map<String, List<RestaurantEntity>> restaurantDataByCategory;
    private Map<Long, RestaurantEntity> restaurantDataById;
    private Map<Long, CustomerEntity> customerDataById;
    private Map<Long, WaitingEntity> waitingDataById;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        restaurantDataByCategory = new HashMap<>();
        restaurantDataById = new HashMap<>();
        customerDataById = new HashMap<>();
        waitingDataById = new HashMap<>();

        initTestData();
    }

    @DisplayName("영업 시간 계산 로직 테스트")
    @Test
    void isRestaurantOpen() {

        boolean open = waitingService.isRestaurantOpen("09:00", "21:00", LocalTime.of(10, 0));
        boolean closed = waitingService.isRestaurantOpen("09:00", "21:00", LocalTime.of(22, 0));

        assertThat(open).isTrue();
        assertThat(closed).isFalse();
    }

    @Test
    @DisplayName("카테고리 별 식당 리스트 조회")
    void getRestaurantListSuccess() {
        // given
        String category = "한식";
        List<RestaurantEntity> koreanRestaurants = restaurantDataByCategory.get(category);
        given(restaurantRepository.findByCategory(category)).willReturn(koreanRestaurants);

        // when
        RestaurantListDTO result = waitingService.getRestaurantList(category);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRestaurantItems()).hasSize(2);
        assertThat(result.getRestaurantItems().get(0).getRestaurantName()).isEqualTo("맛있는 한식당");
        assertThat(result.getRestaurantItems().get(1).getRestaurantName()).isEqualTo("전통 한식집");

        verify(restaurantRepository).findByCategory(category);
    }

    @Test
    @DisplayName("식당 상세 조회")
    void getRestaurantDetail() {
        // given
        Long restaurantId = 1L;
        RestaurantEntity restaurant = restaurantDataById.get(restaurantId);
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

        // when
        RestaurantDetailDTO result = waitingService.getRestaurantDetail(restaurantId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(result.getRestaurantName()).isEqualTo("맛있는 한식당");
        assertThat(result.isOpen()).isTrue();
    }

    @Test
    @DisplayName("웨이팅 등록")
    void registerWaiting() {

        // given
        Long customerId = 2L;
        Long restaurantId = 1L;
        int partySize = 2;

        CustomerEntity customer = customerDataById.get(customerId);
        RestaurantEntity restaurant = restaurantDataById.get(restaurantId);

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(customer));
        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(waitingRepository.findMaxWaitingNumberByRestaurantIdAndRegisteredAt(
                restaurantId, LocalDate.now()))
                .willReturn(Optional.of(1));

        WaitingEntity savedWaiting = WaitingEntity.builder()
                .id(11L)
                .customer(customer)
                .restaurant(restaurant)
                .waitingNumber(2)
                .status(WaitingStatus.APPLIED)
                .registeredAt(LocalDateTime.now())
                .build();

        given(waitingRepository.save(any(WaitingEntity.class)))
                .willReturn(savedWaiting);
        given(waitingRepository.findById(11L))
                .willReturn(Optional.of(savedWaiting));
        given(waitingRepository.countByRestaurantIdAndStatusAndRegisteredAtLessThan(
                restaurantId, WaitingStatus.APPLIED, savedWaiting.getRegisteredAt()))
                .willReturn(1);

        // when
        WaitingRegisterRespDTO result = waitingService.registerWaiting(
                customerId, restaurantId, partySize);

        // then
        assertThat(result.getWaitingNumber()).isEqualTo(2);
        assertThat(result.getRemainingTeamCount()).isEqualTo(1);

    }

    @Test
    @DisplayName("웨이팅 취소")
    void cancelWaiting() {
        // given
        Long restaurantId = 1L;
        Long waitingId = 1L;
        Long customerId = 1L;

        CustomerEntity customer = customerDataById.get(customerId);
        RestaurantEntity restaurant = restaurantDataById.get(restaurantId);
        WaitingEntity waiting = waitingDataById.get(waitingId);
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(waitingRepository.findByIdAndStatus(waitingId, WaitingStatus.APPLIED)).willReturn(waiting);

        // when
        WaitingCancelRespDTO result = waitingService.cancelWaiting(restaurantId, waitingId, new CommonWaitingReqDTO(customerId));

        assertThat(result.getStatus()).isEqualTo(WaitingStatus.CANCELED);
    }

    @Test
    @DisplayName("웨이팅 오버뷰")
    void getWaitingOverView() {
        // given
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Long restaurantId = 1L;
        RestaurantEntity restaurant = restaurantDataById.get(restaurantId);
        given(waitingRepository.countByRestaurantIdAndStatusAndDateRange(restaurantId, WaitingStatus.APPLIED, startOfDay, endOfDay)).willReturn(2);
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

        // when
        WaitingOverviewDTO result = waitingService.getWaitingOverview(restaurantId);

        assertThat(result).isNotNull();
        assertThat(result.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(result.getRemainingTeamCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("내 웨이팅 조회")
    void getMyWaiting() {
        // given
        Long restaurantId = 1L;
        Long customerId = 2L;
        Long waitingId = 2L;

        RestaurantEntity restaurant = restaurantDataById.get(restaurantId);
        WaitingEntity waiting = waitingDataById.get(waitingId);
        CustomerEntity customer = customerDataById.get(customerId);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(waitingRepository.findById(waitingId)).willReturn(Optional.of(waiting));
        given(waitingRepository.countByRestaurantIdAndStatusAndRegisteredAtLessThan(
                restaurantId, WaitingStatus.APPLIED, waiting.getRegisteredAt()))
                .willReturn(1);

        // when
        MyWaitingStatusDTO result = waitingService.getMyWaiting(restaurantId, waitingId, new CommonWaitingReqDTO(customerId));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemainingTeamCount()).isEqualTo(1);

    }

    private void initTestData() {
        RestaurantEntity restaurant1 = RestaurantEntity.builder()
                .id(1L)
                .restaurantName("맛있는 한식당")
                .restaurantAddr("서울시 강남구")
                .restaurantImage("image1.jpg")
                .category("한식")
                .price(20000)
                .openTime("09:00")
                .closeTime("22:00")
                .build();

        RestaurantEntity restaurant2 = RestaurantEntity.builder()
                .id(2L)
                .restaurantName("전통 한식집")
                .restaurantAddr("서울시 서초구")
                .restaurantImage("image2.jpg")
                .category("한식")
                .price(15000)
                .openTime("10:00")
                .closeTime("21:00")
                .build();

        RestaurantEntity restaurant3 = RestaurantEntity.builder()
                .id(3L)
                .restaurantName("이탈리안 레스토랑")
                .restaurantAddr("서울시 종로구")
                .restaurantImage("image3.jpg")
                .category("양식")
                .price(20000)
                .openTime("11:00")
                .closeTime("23:00")
                .build();

        RestaurantEntity restaurant4 = RestaurantEntity.builder()
                .id(4L)
                .restaurantName("스시 전문점")
                .restaurantAddr("서울시 마포구")
                .restaurantImage("image4.jpg")
                .category("일식")
                .price(50000)
                .openTime("12:00")
                .closeTime("22:00")
                .build();

        CustomerEntity customer1 = CustomerEntity.builder()
                .id(1L)
                .customerName("김철수")
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .id(2L)
                .customerName("이영희")
                .build();

        CustomerEntity customer3 = CustomerEntity.builder()
                .id(3L)
                .customerName("박민수")
                .build();

        // Waiting 데이터
        WaitingEntity waiting1 = WaitingEntity.builder()
                .id(1L)
                .customer(customer1)
                .restaurant(restaurant1)
                .waitingNumber(1)
                .status(WaitingStatus.APPLIED)
                .registeredAt(LocalDateTime.now())
                .build();

        WaitingEntity waiting2 = WaitingEntity.builder()
                .id(2L)
                .customer(customer2)
                .restaurant(restaurant1)
                .waitingNumber(2)
                .status(WaitingStatus.APPLIED)
                .registeredAt(LocalDateTime.now())
                .build();

        // 카테고리별 Map에 저장
        restaurantDataByCategory.put("한식", Arrays.asList(restaurant1, restaurant2));
        restaurantDataByCategory.put("양식", Arrays.asList(restaurant3));
        restaurantDataByCategory.put("일식", Arrays.asList(restaurant4));
        restaurantDataByCategory.put("중식", new ArrayList<>()); // 빈 카테고리

        // ID별 Map에 저장
        restaurantDataById.put(1L, restaurant1);
        restaurantDataById.put(2L, restaurant2);
        restaurantDataById.put(3L, restaurant3);
        restaurantDataById.put(4L, restaurant4);

        customerDataById.put(1L, customer1);
        customerDataById.put(2L, customer2);
        customerDataById.put(3L, customer3);

        waitingDataById.put(1L, waiting1);
        waitingDataById.put(2L, waiting2);
    }
}


























