package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is for tool loans.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "loans")
@ToString(exclude = {"client", "loanUnits"})
public class LoansEntity {

    /**
     * Id of the Loan
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loanId", nullable = false, unique = true)
    private Long loanId;

    /**
     * Id of the client associated to loan
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clientId", nullable = false)
    //@JsonBackReference("client-loans")
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storeId", nullable = false)
    private StoreEntity store;

    /**
     * Date of the loan start
     */
    @Column(name = "loanStart", nullable = false)
    private LocalDate loanStart;

    /**
     * Date of the loan end
     */
    @Column(name = "loanEnd", nullable = false)
    private LocalDate loanEnd;

    @Column(name = "returnLoan")
    private LocalDate returnLoan;

    @Column(name = "price", nullable = false)
    private double price;

    /**
     * Unit of the loan
     */
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("loan-loanunit")
    private List<LoanUnitEntity> loanUnits = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "fine")
    private Long fine;

}
