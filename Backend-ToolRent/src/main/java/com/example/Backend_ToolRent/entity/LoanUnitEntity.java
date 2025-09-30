package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

/**
 * This class is to view the loans by tool, allowing you to see the statuses one by one of the deliveries
 */
@Data
@Entity
@Table(name = "loanUnit")
@ToString(exclude = {"loan", "unit"})
public class LoanUnitEntity {

    /**
     * Id of the class
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relation unit with loan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loanId", nullable = false)
    @JsonBackReference("loan-loanunit")
    private LoansEntity loan;

    /**
     * Relation loan with unit
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unitId", nullable = false)
    @JsonBackReference("unit-loanunit")
    private UnitEntity unit;

    /**
     * State of the unit delivered
      */
    @Column(name = "state")
    private String state;

    /**
     * Date of the client return
     */
    @Column(name = "returnDate")
    private LocalDate returnDate;
}
