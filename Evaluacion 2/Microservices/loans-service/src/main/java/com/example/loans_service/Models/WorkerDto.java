package com.example.loans_service.Models;

import lombok.Data;

import java.util.Set;

@Data
public class WorkerDto {
    private Long workerId;
    private Set<String> rol;
    private String name;
    private String mail;
    private String keycloakId;
    private String password;
}
