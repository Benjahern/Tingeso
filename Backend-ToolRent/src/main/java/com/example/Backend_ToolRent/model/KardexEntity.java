package com.example.Backend_ToolRent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * This class is for store movements, generating traceability
 */
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

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "comment")
    private String comment;

}
