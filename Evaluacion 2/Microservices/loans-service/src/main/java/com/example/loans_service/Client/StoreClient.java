package com.example.loans_service.Client;

import com.example.loans_service.Models.StoreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "rates-service")
public interface StoreClient {

    @GetMapping("/store")
    StoreDto getStore();

    @GetMapping("/store/daily-fine")
    Long getDailyFine();
}
