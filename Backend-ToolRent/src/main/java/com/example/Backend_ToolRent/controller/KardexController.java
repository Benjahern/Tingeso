package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.KardexEntity;
import com.example.Backend_ToolRent.service.KardexService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kardex")
@CrossOrigin("*")
public class KardexController {

    private final KardexService kardexService;

    public KardexController(KardexService kardexService) {
        this.kardexService = kardexService;
    }

    @GetMapping("/history/unit/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getKardexHistoryForUnit(@PathVariable Long unitId) {
        List<KardexEntity> history = kardexService.getHistoryForUnit(unitId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getAllKardex() {
        List<KardexEntity> history = kardexService.getAllKardex();
        return ResponseEntity.ok(history);
    }
}
