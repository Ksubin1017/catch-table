package com.project.multimoduledatabase.Service;

import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.entity.MenuEntity;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import com.project.multimoduledatabase.repository.MenuRepository;
import com.project.multimoduledatabase.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<RestaurantListItemDTO> getRestaurantList(RestaurantCategory category){
        List<RestaurantEntity> restaurantEntityList = restaurantRepository.findByCategory(category);

        return restaurantEntityList.stream()
                .map(restaurantEntity -> RestaurantListItemDTO. builder()
                        .id(restaurantEntity.getId())
                        .name(restaurantEntity.getName())
                        .addr(restaurantEntity.getAddr())
                        .image(restaurantEntity.getImage())
                        .category(restaurantEntity.getCategory())
                        .price(restaurantEntity.getPrice())
                        .businessHours(BusinessHoursDTO.builder()
                                .open(restaurantEntity.getOpenTime())
                                .close(restaurantEntity.getCloseTime())
                                .build())
                        .isOpen(isRestaurantOpen(
                                restaurantEntity.getOpenTime(),
                                restaurantEntity.getCloseTime(),
                                LocalTime.now()))
                        .build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestaurantDetailDTO getRestaurantDetail(Long restaurantId){
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Restaurant" + restaurantId));

        List<MenuEntity> menuEntityList = menuRepository.findByRestaurant_id(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Menu" + restaurantId));

        List<MenuDTO> menuList = menuEntityList.stream()
                .map(menuEntity -> MenuDTO.builder()
                        .id(menuEntity.getId())
                        .name(menuEntity.getName())
                        .price(menuEntity.getPrice())
                        .build()).collect(Collectors.toList());

        return RestaurantDetailDTO.builder()
                .id(restaurantId)
                .name(restaurantEntity.getName())
                .addr(restaurantEntity.getAddr())
                .image(restaurantEntity.getImage())
                .menuList(menuList)
                .businessHours(BusinessHoursDTO.builder()
                        .open(restaurantEntity.getOpenTime())
                        .close(restaurantEntity.getCloseTime())
                        .build())
                .isOpen(isRestaurantOpen(restaurantEntity.getOpenTime(), restaurantEntity.getCloseTime(), LocalTime.now()))
                .build();
    }

    public boolean isRestaurantOpen(String openTime, String closeTime, LocalTime now) {
        LocalTime open = LocalTime.parse(openTime);
        LocalTime close = LocalTime.parse(closeTime);
        return now.isAfter(open) && now.isBefore(close);
    }
}
