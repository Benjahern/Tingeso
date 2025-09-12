package com.example.Backend_ToolRent.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Rol")
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rolId", nullable = false, unique = true)
    private Long rolId;

    @Column(name = "rolName", nullable = false, unique = true)
    private String rolName;
}
