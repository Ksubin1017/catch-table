package com.project.catchtable.controller;

import com.project.multimoduledatabase.Service.RestaurantService;
import com.project.multimoduledatabase.common.CommonCode;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.RestaurantDetailDTO;
import com.project.multimoduledatabase.dto.RestaurantListItemDTO;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RestaurantControllerTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("카테고리별 식당 조회 - KOREAN")
    void getRestaurants() throws Exception {

        // Given
        RestaurantCategory category = RestaurantCategory.KOREAN;

        List<RestaurantListItemDTO> expected = restaurantService.getRestaurantList(category);

        // When
        ResponseEntity<CommonResp<List<RestaurantListItemDTO>>> response =
                restTemplate.exchange(
                        "/restaurant/{category}",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        },
                        category
                );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getBody().getData().size()).isEqualTo(expected.size());
    }

    @Test
    @DisplayName("식당 상세 조회")
    void getRestaurantDetail() throws Exception {

        // Given
        Long id = 1L;

        RestaurantDetailDTO expected = restaurantService.getRestaurantDetail(id);

        // When
        ResponseEntity<CommonResp<RestaurantDetailDTO>> response =
                restTemplate.exchange(
                        "/restaurant/{id}/detail",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CommonResp<RestaurantDetailDTO>>() {},
                        id
                );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(CommonCode.SUCCESS);
        assertThat(response.getBody().getData().getName()).isEqualTo("김밥천국");
    }
}