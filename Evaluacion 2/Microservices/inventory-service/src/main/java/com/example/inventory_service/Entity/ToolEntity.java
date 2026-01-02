package com.example.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tools")
public class ToolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toolId", nullable = false, unique = true)
    private long toolId;

    @Column(name = "toolName", nullable = false, unique = true)
    private String toolName;

    @Column(name = "toolDescription")
    private String toolDescription;

    @Column(name = "replacementValue", nullable = false)
    private Double replacementValue;

    @Column(name = "category")
    private String category;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "dailyPrice", nullable = false)
    private Double dailyPrice;

    @Column(name = "imagenPath")
    private String imagePath;
}
