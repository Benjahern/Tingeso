package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.KardexEntity;
import com.example.Backend_ToolRent.service.KardexService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @GetMapping("/history/tool/{toolId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getKardexHistoryForTool(@PathVariable Long toolId) {
        List<KardexEntity> history = kardexService.getHistoryForTool(toolId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Object[]>> getRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        System.out.println("========== RANKING REQUEST ==========");
        System.out.println("📥 Fecha Inicio recibida del frontend: " + fechaInicio);
        System.out.println("📥 Fecha Fin recibida del frontend: " + fechaFin);

        List<Object[]> ranking;

        if (fechaInicio == null && fechaFin == null) {
            LocalDate now = LocalDate.now();
            fechaInicio = now.withDayOfMonth(1);
            fechaFin = now.withDayOfMonth(now.lengthOfMonth());
            System.out.println("⚠️ Usando mes actual por defecto");
        }

        LocalDateTime startDate = fechaInicio.atStartOfDay();
        LocalDateTime endDate = fechaFin.atTime(23, 59, 59);

        System.out.println("🔍 Consultando con fechas:");
        System.out.println("   Inicio: " + startDate);
        System.out.println("   Fin: " + endDate);

        ranking = kardexService.getRankingTool(startDate, endDate);

        System.out.println("📊 Resultados obtenidos: " + ranking.size());
        System.out.println("=====================================");

        return ResponseEntity.ok(ranking);
    }



}
