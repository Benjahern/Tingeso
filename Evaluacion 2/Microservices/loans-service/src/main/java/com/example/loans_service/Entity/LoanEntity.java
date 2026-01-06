package com.example.loans_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "loans")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @Column(name = "clientId", nullable = false)
    private Long clientId; // Solo ID, cliente está en otro servicio

    @Column(name = "loanStart", nullable = false)
    private LocalDate loanStart;

    @Column(name = "loanEnd", nullable = false)
    private LocalDate loanEnd;

    @Column(name = "returnLoan")
    private LocalDate returnLoan;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "fine")
    private Long fine;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanUnitEntity> loanUnits = new java.util.ArrayList<>(); // ✅ Esta lista SÍ se mantiene (mismo
                                                                          // servicio)
}