package com.example.loans_service.Client;

import com.example.loans_service.Models.ClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "clients-service")
public interface ClientClient {

    @GetMapping("/clients/rut")
    List<ClientDto> findByRut(@RequestParam String rut);

    @GetMapping("/clients/{id}")
    ClientDto findById(@PathVariable Long id);

    @GetMapping("/clients/name")
    List<ClientDto> findByName(@RequestParam String name);

    @PostMapping("/clients/{id}/debt/add")
    ClientDto addDebt(@PathVariable Long id, @RequestBody Map<String, Double> body);

    @PostMapping("/clients/{id}/state")
    ClientDto setState(@PathVariable Long id, @RequestParam String state);
}
