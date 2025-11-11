package com.project.catchtable.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.multimoduledatabase.Service.RestaurantService;
import com.project.multimoduledatabase.common.CommonResp;
import com.project.multimoduledatabase.dto.RestaurantDetailDTO;
import com.project.multimoduledatabase.dto.RestaurantListItemDTO;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("카테고리별 식당 조회 - KOREAN")
    void getRestaurants() throws Exception {

        // Given
        RestaurantCategory category = RestaurantCategory.KOREAN;

        List<RestaurantListItemDTO> expected = restaurantService.getRestaurantList(category);

        // When
        MvcResult result = mockMvc.perform(get("/restaurant/{category}", category)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CommonResp<List<RestaurantListItemDTO>> response =
                objectMapper.readValue(body, new TypeReference<CommonResp<List<RestaurantListItemDTO>>>() {
                });

        assertThat(response.getCode()).isEqualTo(1000);
        assertThat(response.getData()).isNotEmpty();
        assertThat(response.getData().size()).isEqualTo(expected.size());
    }

    @Test
    @DisplayName("식당 상세 조회")
    void getRestaurantDetail() throws Exception {

        // Given
        Long id = 1L;

        RestaurantDetailDTO expected = restaurantService.getRestaurantDetail(id);

        // When
        MvcResult result = mockMvc.perform(get("/restaurant/{id}/detail", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CommonResp<RestaurantDetailDTO> response =
                objectMapper.readValue(body, new TypeReference<CommonResp<RestaurantDetailDTO>>() {
                });

        assertThat(response.getCode()).isEqualTo(1000);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).isEqualTo(expected);
    }
}
