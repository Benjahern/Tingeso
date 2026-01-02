package com.example.kardex_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "kardex")
public class KardexEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kardexId", unique = true, nullable = false)
    private Long kardexId;

    @Column(name = "unitId", nullable = false)
    private Long unitId;

    @Column(name = "workerId", nullable = false)
    private Long workerId;

    @Column(name = "loanId")
    private Long loanId;

    @Column(name = "movement", nullable = false)
    private String movement;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    // 1 for entrence, -1 for exit
    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "stockBalance", nullable = false)
    private Integer stockBalance;

    @Column(name = "unitCost")
    private BigDecimal unitCost;

    @Column(name = "totalValue")
    private BigDecimal totalValue;

    @Column(name = "comment")
    private String comment;
}
