package com.example.Backend_ToolRent.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

/**
 * class of the store
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "store")
public class StoreEntity {

    /**
     * Id of the store
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId", unique = true, nullable = false)
    private Long storeId;

    /**
     * Name of the store
     */
    @Column(name = "name")
    private String storeName;

    /**
     * Address of the store
     */
    @Column(name = "address")
    private String storeAddress;

    /**
     * Daily fine of the store
     */
    @Column(name = "dailyFine", nullable = false)
    private int dailyFine;

}
