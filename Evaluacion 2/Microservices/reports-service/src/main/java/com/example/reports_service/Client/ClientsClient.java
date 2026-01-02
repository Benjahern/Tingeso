package com.example.reports_service.Client;

import com.example.reports_service.Models.ClientDebtDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "clients-service")
public interface ClientsClient {

    @GetMapping("/clients/with-debt")
    List<ClientDebtDto> getClientsWithDebt();

    @GetMapping("/clients/{id}")
    ClientDebtDto getClientById(@PathVariable Long id);
}
