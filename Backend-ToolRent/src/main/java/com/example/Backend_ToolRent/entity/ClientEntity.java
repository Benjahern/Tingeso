package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Entity client of ToolRent
 */
@Data
@Entity
@Table(name = "clients")
public class ClientEntity extends UserEntity {

    /**
     * Phone number of the client
     */
    @Column(name = "phoneNumber", nullable = false)
    private String phone;

    /**
     * Address of the client
     */
    @Column(name = "addrress")
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
    @Column(name = "rut", nullable = false)
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
    @JsonManagedReference
    private List<LoansEntity> loans;
}
