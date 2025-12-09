package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Class for the workers
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "worker")
public class WorkerEntity extends UserEntity {

    /**
     * Rol of the user
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workerRol")
    private Set<RolEntity> rol;

    @Column(name = "keycloakId")
    public String keycloakId;

    /**
     * Password caof the workers, for login
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * FK of the store where the worker works
     */
    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private StoreEntity store;
}
