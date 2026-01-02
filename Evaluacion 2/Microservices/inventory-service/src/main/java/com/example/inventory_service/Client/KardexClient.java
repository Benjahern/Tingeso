package com.example.inventory_service.Client;

import com.example.inventory_service.Models.KardexDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kardex-service")
public interface KardexClient {

    @PostMapping("/kardex")
    KardexDto createMovement(@RequestBody KardexDto kardexDto);
}
