package com.project.multimoduledatabase.Service;

import com.project.multimoduledatabase.dto.*;
import com.project.multimoduledatabase.entity.MenuEntity;
import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import com.project.multimoduledatabase.repository.MenuRepository;
import com.project.multimoduledatabase.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "restaurants", key = "#p0")
    public List<RestaurantListItemDTO> getRestaurantList(RestaurantCategory category) {
        List<RestaurantEntity> restaurantEntityList = restaurantRepository.findByCategory(category);

        return restaurantEntityList.stream()
                .map(restaurantEntity -> new RestaurantListItemDTO(
                        restaurantEntity.getId(),
                        restaurantEntity.getName(),
                        restaurantEntity.getAddr(),
                        restaurantEntity.getImage(),
                        restaurantEntity.getCategory(),
                        restaurantEntity.getPrice(),
                        new BusinessHoursDTO(
                                restaurantEntity.getOpenTime(),
                                restaurantEntity.getCloseTime()),
                        isRestaurantOpen(
                                restaurantEntity.getOpenTime(),
                                restaurantEntity.getCloseTime(),
                                LocalTime.now())

                )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestaurantDetailDTO getRestaurantDetail(Long restaurantId) {
        CompletableFuture<RestaurantEntity> restaurantFuture =
                CompletableFuture.supplyAsync(() -> restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new IllegalArgumentException("Not Found Restaurant" + restaurantId)));

        CompletableFuture<List<MenuEntity>> menuFuture =
                CompletableFuture.supplyAsync(() -> menuRepository.findByRestaurant_id(restaurantId)
                        .orElseThrow(() -> new IllegalArgumentException("Not Found Restaurant" + restaurantId)));

//        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("Not Found Restaurant" + restaurantId));

//        List<MenuEntity> menuEntityList = menuRepository.findByRestaurant_id(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("Not Found Menu" + restaurantId));

        CompletableFuture.allOf(restaurantFuture, menuFuture).join();
        RestaurantEntity restaurantEntity = restaurantFuture.join();
        List<MenuEntity> menuEntityList = menuFuture.join();

        List<MenuDTO> menuList = menuEntityList.stream()
                .map(menuEntity -> new MenuDTO(
                        menuEntity.getId(),
                        menuEntity.getName(),
                        menuEntity.getPrice(),
                        menuEntity.getImage())
                ).collect(Collectors.toList());

        return new RestaurantDetailDTO(
                restaurantId,
                restaurantEntity.getName(),
                restaurantEntity.getAddr(),
                restaurantEntity.getImage(),
                menuList,
                isRestaurantOpen(restaurantEntity.getOpenTime(), restaurantEntity.getCloseTime(), LocalTime.now()),
                new BusinessHoursDTO(
                        restaurantEntity.getOpenTime()
                        , restaurantEntity.getCloseTime()));

    }

    public Boolean isRestaurantOpen(String openTime, String closeTime, LocalTime now) {
        LocalTime open = LocalTime.parse(openTime);
        LocalTime close = LocalTime.parse(closeTime);
        return now.isAfter(open) && now.isBefore(close);
    }
}