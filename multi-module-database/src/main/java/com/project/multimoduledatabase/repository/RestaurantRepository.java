package com.project.multimoduledatabase.repository;

import com.project.multimoduledatabase.entity.RestaurantEntity;
import com.project.multimoduledatabase.enums.RestaurantCategory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    @Cacheable(value = "restaurants", key = "#category")
    List<RestaurantEntity> findByCategory(RestaurantCategory category);
}
