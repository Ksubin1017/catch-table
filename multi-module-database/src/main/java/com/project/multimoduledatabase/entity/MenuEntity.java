package com.project.multimoduledatabase.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuName;
    private int menuPrice;
    private String menuImage;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;
}
