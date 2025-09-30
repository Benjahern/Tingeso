package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.StoreEntity;
import com.example.Backend_ToolRent.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;

import java.util.Map;


@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;


    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreEntity> getStoreById(@PathVariable Long id) {
        try {
            StoreEntity store = storeService.getStoreById(id);
            return ResponseEntity.ok(store);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreEntity> createStore(@RequestBody StoreEntity storeEntity) {
        StoreEntity savedStore = storeService.saveStore(storeEntity);
        return new ResponseEntity<>(savedStore, HttpStatus.CREATED);
    }


    @PutMapping("/{id}/daily-fine")
    public ResponseEntity<StoreEntity> updateDailyFine(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> pay) {

        Integer newFine = pay.get("newFine");
        try {
            StoreEntity updatedStore = storeService.updateDailyFine(id, newFine);
            return ResponseEntity.ok(updatedStore);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}