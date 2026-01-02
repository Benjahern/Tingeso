package com.example.loans_service.Models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KardexDto {

    private Long unitId;
    private Long workerId;
    private Long loanId;
    private String movement;
    private Integer type;
    private Integer stockBalance;
    private BigDecimal unitCost;
    private BigDecimal totalValue;
    private String comment;

}
