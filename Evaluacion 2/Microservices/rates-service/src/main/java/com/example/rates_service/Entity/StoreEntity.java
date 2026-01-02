package com.example.rates_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "store")
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @Column(name = "daily_fine", nullable = false)
    private Long dailyFine;
}
