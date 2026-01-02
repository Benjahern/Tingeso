package com.example.loans_service.Controller;

import com.example.loans_service.Entity.LoanUnitEntity;
import com.example.loans_service.Service.LoanUnitService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/loan/unit")
@RestController
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
