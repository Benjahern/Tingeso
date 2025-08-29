package com.example.Backend_ToolRent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Class for the workers
 */
@Data
@Entity
@Table(name = "worker")
public class WorkerEntity extends UserEntity {

    /**
     * Rol of the user
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workerRol")
    private List<RolEntity> rol;

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
