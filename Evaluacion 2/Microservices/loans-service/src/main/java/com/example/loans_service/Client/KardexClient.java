package com.example.loans_service.Client;

import com.example.loans_service.Models.KardexDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kardex-service")
public interface KardexClient {

    @PostMapping("/kardex")
    KardexDto createMovement(@RequestBody KardexDto kardexDto);

    @DeleteMapping("/kardex/loan/{loanId}")
    KardexDto deleteByLoanId(@PathVariable("loanId") Long loanId);


}
