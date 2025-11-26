package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Entity client of ToolRent
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "clients")
public class ClientEntity extends UserEntity {

    /**
     * Phone number of the client
     */
    @Column(name = "phone", nullable = false)
    private String phone;

    /**
     * Address of the client
     */
    @Column(name = "address")
    private String address;

    /**
     * State of the client
     * "Activo" in this state the client can ask for loans
     * "Restringido" in this state the client has blocked the loans
     */
    @Column(name = "state")
    private String state;

    /**
     * RUT of the client
      */
    @Column(name = "rut", nullable = false, unique = true)
    private String rut;

    /**
     * Debt if applicable
     */
    @Column(name = "debt")
    private double debt;

    /**
     * List of client loans
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("client-loans")
    private List<LoansEntity> loans;
}
