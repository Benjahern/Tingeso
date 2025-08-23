package com.example.Backend_ToolRent.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

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
    private String debt;

    /**
     * List of client loans
     */
    /*
    @OneToMany(mappedBy = "client")
    @JsonIgnoreProperties("client")
    private List<LoansEntity> loans;
     */
}
