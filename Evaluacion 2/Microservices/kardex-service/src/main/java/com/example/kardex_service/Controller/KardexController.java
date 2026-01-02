package com.example.kardex_service.Controller;

import com.example.kardex_service.Entity.KardexEntity;
import com.example.kardex_service.Service.KardexService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/kardex")
public class KardexController {

    private final KardexService kardexService;

    public KardexController(KardexService kardexService) {
        this.kardexService = kardexService;
    }

    // ==================== CREATE ====================

    /**
     * Registrar un nuevo movimiento en el kardex
     * POST /kardex
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<Void> createMovement(@RequestBody KardexEntity kardex) {
        kardexService.registerMovement(
                kardex.getUnitId(),
                kardex.getMovement(),
                kardex.getWorkerId(),
                kardex.getLoanId(),
                kardex.getComment(),
                kardex.getStockBalance(),
                kardex.getUnitCost(),
                kardex.getTotalValue());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ==================== READ ====================

    /**
     * Obtener todos los registros del kardex
     * GET /kardex
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getAllKardex() {
        List<KardexEntity> kardexList = kardexService.getAllKardex();
        return ResponseEntity.ok(kardexList);
    }

    /**
     * Obtener historial de kardex por unidad
     * GET /kardex/unit/{unitId}
     */
    @GetMapping("/unit/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getHistoryByUnit(@PathVariable Long unitId) {
        List<KardexEntity> history = kardexService.getHistoryForUnit(unitId);
        return ResponseEntity.ok(history);
    }

    /**
     * Obtener historial de kardex por préstamo
     * GET /kardex/loan/{loanId}
     */
    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getHistoryByLoan(@PathVariable Long loanId) {
        List<KardexEntity> history = kardexService.getHistoryByLoan(loanId);
        return ResponseEntity.ok(history);
    }

    /**
     * Obtener historial de kardex por trabajador
     * GET /kardex/worker/{workerId}
     */
    @GetMapping("/worker/{workerId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getHistoryByWorker(@PathVariable Long workerId) {
        List<KardexEntity> history = kardexService.getHistoryByWorker(workerId);
        return ResponseEntity.ok(history);
    }

    /**
     * Obtener kardex por tipo de movimiento
     * GET /kardex/movement/{movement}
     */
    @GetMapping("/movement/{movement}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getByMovement(@PathVariable String movement) {
        List<KardexEntity> kardexList = kardexService.getKardexByMovement(movement);
        return ResponseEntity.ok(kardexList);
    }

    /**
     * Obtener kardex por rango de fechas
     * GET /kardex/date-range?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<KardexEntity> kardexList = kardexService.getKardexByDateRange(start, end);
        return ResponseEntity.ok(kardexList);
    }

    /**
     * Obtener kardex por unidad y tipo de movimiento
     * GET /kardex/unit/{unitId}/movement/{movement}
     */
    @GetMapping("/unit/{unitId}/movement/{movement}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<KardexEntity>> getByUnitAndMovement(
            @PathVariable Long unitId,
            @PathVariable String movement) {
        List<KardexEntity> kardexList = kardexService.getKardexByUnitAndMovement(unitId, movement);
        return ResponseEntity.ok(kardexList);
    }

    /**
     * Contar movimientos por unidad
     * GET /kardex/unit/{unitId}/count
     */
    @GetMapping("/unit/{unitId}/count")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<Long> countByUnit(@PathVariable Long unitId) {
        long count = kardexService.countMovementsByUnit(unitId);
        return ResponseEntity.ok(count);
    }

    // ==================== DELETE ====================

    /**
     * Eliminar registros de kardex por préstamo
     * DELETE /kardex/loan/{loanId}
     */
    @DeleteMapping("/loan/{loanId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByLoan(@PathVariable Long loanId) {
        kardexService.deleteByLoanId(loanId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Object[]>> getRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        System.out.println("========== RANKING REQUEST ==========");
        System.out.println(" Fecha Inicio recibida del frontend: " + fechaInicio);
        System.out.println(" Fecha Fin recibida del frontend: " + fechaFin);

        List<Object[]> ranking;

        if (fechaInicio == null && fechaFin == null) {
            LocalDate now = LocalDate.now();
            fechaInicio = now.withDayOfMonth(1);
            fechaFin = now.withDayOfMonth(now.lengthOfMonth());
            System.out.println(" Usando mes actual por defecto");
        }

        LocalDateTime startDate = fechaInicio.atStartOfDay();
        LocalDateTime endDate = fechaFin.atTime(23, 59, 59);

        System.out.println("  Consultando con fechas:");
        System.out.println("   Inicio: " + startDate);
        System.out.println("   Fin: " + endDate);

        ranking = kardexService.getRankingUnits(startDate, endDate);

        System.out.println(" Resultados obtenidos: " + ranking.size());
        System.out.println("=====================================");

        return ResponseEntity.ok(ranking);
    }

}
