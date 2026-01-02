package com.example.rates_service.Controller;

import com.example.rates_service.Entity.StoreEntity;
import com.example.rates_service.Service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<StoreEntity> getStore() {
        return ResponseEntity.ok(storeService.getStore());
    }

    @GetMapping("/daily-fine")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<Long> getDailyFine() {
        return ResponseEntity.ok(storeService.getDailyFine());
    }

    @PutMapping("/daily-fine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreEntity> updateDailyFine(@RequestBody Map<String, Long> body) {
        Long dailyFine = body.get("dailyFine");
        return ResponseEntity.ok(storeService.updateDailyFine(dailyFine));
    }
}
