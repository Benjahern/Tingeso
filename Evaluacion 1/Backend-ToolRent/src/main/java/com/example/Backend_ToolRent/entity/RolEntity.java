package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

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
