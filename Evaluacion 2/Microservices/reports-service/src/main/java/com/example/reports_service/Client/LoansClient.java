package com.example.reports_service.Client;

import com.example.reports_service.Models.LoanDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "loans-service")
public interface LoansClient {

    @GetMapping("/loans/active")
    List<LoanDto> getActiveLoans();
}
