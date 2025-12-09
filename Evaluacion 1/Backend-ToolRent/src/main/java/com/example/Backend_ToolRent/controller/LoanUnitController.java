package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.LoanUnitEntity;
import com.example.Backend_ToolRent.service.LoanUnitService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan/unit")
@CrossOrigin("*")
public class LoanUnitController {

    private final LoanUnitService loanUnitService;

    public LoanUnitController(LoanUnitService loanUnitService) {
        this.loanUnitService = loanUnitService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public LoanUnitEntity createLoanUnit(LoanUnitEntity loanUnitEntity) {
        return loanUnitService.createLoanUnit(loanUnitEntity);
    }


}
