package com.example.loans_service.Models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class ClientDto {

    private Long clientId;

    private String name;

    private String phone;

    private String mail;

    private String state;

    private String rut;

    private Double debt;
}
