package com.example.users_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

/**
 * Class for the workers
 */
@Data
@Entity
@Table(name = "worker")
public class WorkerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workerId", nullable = false, unique = true)
    private Long workerId;

    /**
     * Rol of the user
     */
    @ElementCollection
    @CollectionTable(name = "worker_roles", joinColumns = @JoinColumn(name = "worker_id"))
    @Column(name = "rol")
    private Set<String> rol;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mail", nullable = false)
    private String mail;

    /**
     * Id asociated to keycloak
     */
    @Column(name = "keycloakId")
    public String keycloakId;

    /**
     * Password of the workers, for login
     */
    @Column(name = "password", nullable = false)
    private String password;

}
