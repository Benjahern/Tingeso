package com.example.clients_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "clients")
public class ClientsEntity {

    /**
     * Id of the client
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clientId", nullable = false, unique = true)
    private Long clientId;

    /**
     * Name of the client
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Phone number of the client
     */
    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "mail")
    private String mail;

    /**
     * State of the client
     * "Activo" in this state can ask for a loan
     * "Restringido" in this state the client has blocked the loan
     */
    @Column(name = "state")
    private String state;

    /**
     * Rut of the client
     */
    @Column(name = "rut", unique = true)
    private String rut;

    /**
     * Debt of the client
     */
    @Column(name = "debt")
    private Double debt;

}
