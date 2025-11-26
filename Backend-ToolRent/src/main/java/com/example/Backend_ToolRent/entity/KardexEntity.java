package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This class is for store movements, generating traceability
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "inventory")
public class KardexEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kardexId", nullable = false, unique = true)
    private Long kardexId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unitId", nullable = false)
    private UnitEntity unit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storeId", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workerId", nullable = false)
    private WorkerEntity worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loanId")
    private LoansEntity loan;

    @Column(name = "movement", nullable = false)
    private String movement;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

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
