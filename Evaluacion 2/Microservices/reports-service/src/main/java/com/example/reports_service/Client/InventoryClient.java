package com.example.reports_service.Client;

import com.example.reports_service.Models.UnitDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/units/{id}")
    UnitDto getUnitById(@PathVariable Long id);
}
