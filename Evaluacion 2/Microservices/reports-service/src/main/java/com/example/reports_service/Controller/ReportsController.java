package com.example.reports_service.Controller;

import com.example.reports_service.Models.*;
import com.example.reports_service.Service.ReportsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    /**
     * Ranking de herramientas más prestadas con nombres.
     * GET /reports/ranking?fechaInicio=2024-01-01&fechaFin=2024-12-31
     */
    @GetMapping("/ranking")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<RankingItemDto>> getToolRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<RankingItemDto> ranking = reportsService.getToolRanking(fechaInicio, fechaFin);
        return ResponseEntity.ok(ranking);
    }

    /**
     * Clientes con deuda pendiente.
     * GET /reports/clients-with-debt
     */
    @GetMapping("/clients-with-debt")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientDebtDto>> getClientsWithDebt() {
        List<ClientDebtDto> clients = reportsService.getClientsWithDebt();
        return ResponseEntity.ok(clients);
    }

    /**
     * Préstamos activos con información del cliente y herramientas.
     * GET /reports/active-loans
     */
    @GetMapping("/active-loans")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ActiveLoanDto>> getActiveLoans() {
        List<ActiveLoanDto> loans = reportsService.getActiveLoansReport();
        return ResponseEntity.ok(loans);
    }
}
