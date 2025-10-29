package com.project.multimoduledatabase.entity;

import com.project.multimoduledatabase.enums.WaitingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

    @Enumerated(EnumType.STRING)
    private WaitingStatus status;

    private int waitingNumber;
    private LocalDateTime registeredAt;

    public void updateStatus(WaitingStatus status) {
        this.status = status;
    }

    public void updateWaitingNumber(int waitingNumber) {
        this.waitingNumber = waitingNumber;
    }
}


