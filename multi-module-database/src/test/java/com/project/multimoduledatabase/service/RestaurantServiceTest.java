package com.project.multimoduledatabase.service;

import com.project.multimoduledatabase.Service.RestaurantService;
import com.project.multimoduledatabase.dto.RestaurantDetailDTO;
import com.project.multimoduledatabase.dto.RestaurantListItemDTO;
import com.project.multimoduledatabase.entity.MenuEntity;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import com.project.multimoduledatabase.repository.MenuRepository;
import com.project.multimoduledatabase.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Map<String, List<RestaurantEntity>> restaurantDataByCategory;
    private Map<Long, RestaurantEntity> restaurantDataById;

    @BeforeEach
    void setUp() {

        restaurantDataByCategory = new HashMap<>();
        restaurantDataById = new HashMap<>();
        initTestData();
    }

    @DisplayName("영업 시간 계산 로직 테스트 - 오픈")
    @Test
    void isRestaurantOpen() {

        boolean open = restaurantService.isRestaurantOpen("09:00", "21:00", LocalTime.of(10, 0));
        assertThat(open).isTrue();
    }

    @DisplayName("영업 시간 계산 로직 테스트 - 클로즈")
    @Test
    void isRestaurantClosed() {

        boolean closed = restaurantService.isRestaurantOpen("09:00", "21:00", LocalTime.of(22, 0));
        assertThat(closed).isFalse();
    }

    @Test
    @DisplayName("카테고리 별 식당 리스트 조회")
    void getRestaurants() {

        // given
        String category = "한식";
        List<RestaurantEntity> koreanRestaurants = restaurantDataByCategory.get(category);
        given(restaurantRepository.findByCategory(RestaurantCategory.KOREAN)).willReturn(koreanRestaurants);

        // when
        List<RestaurantListItemDTO> result = restaurantService.getRestaurantList(RestaurantCategory.KOREAN);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("맛있는 한식당");
        assertThat(result.get(1).getName()).isEqualTo("전통 한식집");
    }

    @Test
    @DisplayName("식당 상세 조회")
    void getRestaurantDetail() {
        // given
        Long restaurantId = 1L;
        RestaurantEntity restaurant = restaurantDataById.get(restaurantId);
        List<MenuEntity> menuList = Arrays.asList(
                new MenuEntity(1L, "김치찌개",  8000, "image", restaurant)
        );
        
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuRepository.findByRestaurant_id(restaurantId)).willReturn(Optional.of(menuList));

        // when
        RestaurantDetailDTO result = restaurantService.getRestaurantDetail(restaurantId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurantId);
        assertThat(result.getName()).isEqualTo("맛있는 한식당");
        assertThat(result.getIsOpen()).isTrue();
    }

    private void initTestData() {
        RestaurantEntity restaurant1 = new RestaurantEntity(
                1L,
                "맛있는 한식당",
                "서울시 강남구",
                "image1.jpg",
                RestaurantCategory.KOREAN,
                20000,
                "09:00",
                "22:00",
                "맛있는 한식당"
        );


        RestaurantEntity restaurant2 = new RestaurantEntity(
                2L,
                "전통 한식집",
                "서울시 서초구",
                "image2.jpg",
                RestaurantCategory.KOREAN,
                15000,
                "10:00",
                "21:00",
                "전통 한식집"
        );

        RestaurantEntity restaurant3 = new RestaurantEntity(
                3L,
                "이탈리안 레스토랑",
                "서울시 종로구",
                "image3.jpg",
                RestaurantCategory.WESTERN,
                20000,
                "11:00",
                "23:00",
                "이탈리안 레스토랑");

        RestaurantEntity restaurant4 = new RestaurantEntity(
                4L,
                "스시 전문점",
                "서울시 마포구",
                "image4.jpg",
                RestaurantCategory.JAPANESE,
                50000,
                "12:00",
                "22:00",
                "스시 전문점");


        restaurantDataByCategory.put("한식", Arrays.asList(restaurant1, restaurant2));
        restaurantDataByCategory.put("양식", Arrays.asList(restaurant3));
        restaurantDataByCategory.put("일식", Arrays.asList(restaurant4));

        restaurantDataById.put(1L, restaurant1);
        restaurantDataById.put(2L, restaurant2);
        restaurantDataById.put(3L, restaurant3);
        restaurantDataById.put(4L, restaurant4);
    }
}
