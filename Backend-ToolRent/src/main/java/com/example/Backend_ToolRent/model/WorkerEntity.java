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
     * 1 for admin
     * 2 for employee
     */
    @Column(name = "rol", nullable = false)
    private List<Integer> rol;

    /**
     * Password of the workers, for login
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
