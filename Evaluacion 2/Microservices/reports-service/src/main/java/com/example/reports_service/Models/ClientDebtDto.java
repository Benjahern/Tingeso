package com.example.reports_service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDebtDto {
    private Long clientId;
    private String name;
    private String rut;
    private String phone;
    private String mail;
    private String state;
    private Double debt;
}
