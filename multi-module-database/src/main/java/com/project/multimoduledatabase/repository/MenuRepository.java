package com.project.multimoduledatabase.repository;

import com.project.multimoduledatabase.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    Optional<List<MenuEntity>> findByRestaurant_id(Long restaurantId);
}
