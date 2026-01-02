package com.example.reports_service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveLoanDto {
    private Long loanId;
    private String clientName;
    private String clientRut;
    private LocalDate loanStart;
    private LocalDate loanEnd;
    private Long daysRemaining;
    private boolean isOverdue;
    private List<String> toolNames;
}
