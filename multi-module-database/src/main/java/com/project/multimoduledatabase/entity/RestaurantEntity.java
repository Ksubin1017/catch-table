package com.project.multimoduledatabase.entity;

import com.project.multimoduledatabase.enums.RestaurantCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String addr;

    @Column(name = "image", nullable = false)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private RestaurantCategory category;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "open_time", nullable = false)
    private String openTime;

    @Column(name = "close_time", nullable = false)
    private String closeTime;

    @Column(name = "webhook_url")
    private String webhookUrl;

}
