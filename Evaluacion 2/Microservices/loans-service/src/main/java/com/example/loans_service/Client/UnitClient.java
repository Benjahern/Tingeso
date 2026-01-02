package com.example.loans_service.Client;

import com.example.loans_service.Models.UnitDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", contextId = "unitClient")
public interface UnitClient {

    @PutMapping("/units/{id}")
    UnitDto updateUnit(@PathVariable("id") Long id, @RequestBody UnitDto unitDto);

    @GetMapping("/units/{id}")
    UnitDto getUnitById(@PathVariable("id") Long id);

    @GetMapping("/units/available/by-tool/{toolId}")
    UnitDto getFirstAvailableByToolId(@PathVariable("toolId") Long toolId);
}
