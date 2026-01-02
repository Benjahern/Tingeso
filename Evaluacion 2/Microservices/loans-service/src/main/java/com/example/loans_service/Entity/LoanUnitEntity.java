package com.example.loans_service.Entity;

import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "loanUnit")
public class LoanUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loanUnitId", unique = true, nullable = false)
    private Long loanUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loanId", nullable = false)
    private LoanEntity loan; //Mantiene relación JPA (mismo servicio)

    @Column(name = "unitId", nullable = false)
    private Long unitId; //Solo el ID, NO el objeto UnitEntity

    @Column(name = "state")
    private String state;

    @Column(name = "returnDate")
    private LocalDate returnDate;
}