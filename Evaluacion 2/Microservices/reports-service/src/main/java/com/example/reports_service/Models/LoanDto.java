package com.example.reports_service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {
    private Long loanId;
    private Long clientId;
    private LocalDate loanStart;
    private LocalDate loanEnd;
    private LocalDate returnLoan;
    private boolean active;
    private double price;
    private Long fine;
    private List<LoanUnitDto> loanUnits;
}
