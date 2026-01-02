package com.example.loans_service.Controller;

import com.example.loans_service.Client.WorkerClient;
import com.example.loans_service.Entity.LoanEntity;
import com.example.loans_service.Models.ClientDto;
import com.example.loans_service.Service.LoanService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loansService;
    private final WorkerClient workerClient;

    public LoanController(LoanService loansService, WorkerClient workerClient) {
        this.loansService = loansService;
        this.workerClient = workerClient;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<LoanEntity> getLoanById(@PathVariable Long id) {
        LoanEntity loan = loansService.getLoanById(id);
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getActiveLoans() {
        List<LoanEntity> activeLoans = loansService.getLoansByActive();
        return new ResponseEntity<>(activeLoans, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> removeLoanById(@PathVariable Long id) {
        loansService.removeLoansById(id);
        return ResponseEntity.ok(Map.of("message", "Prestamo eliminado exitosamente"));
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getInactiveLoans() {
        List<LoanEntity> inactiveLoans = loansService.getLoansByInactive();
        return new ResponseEntity<>(inactiveLoans, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getAllLoans() {
        List<LoanEntity> allLoans = loansService.getAllLoans();
        return new ResponseEntity<>(allLoans, HttpStatus.OK);
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getAllLoansByClientId(@PathVariable Long clientId) {
        List<LoanEntity> loans = loansService.getLoansByClientId(clientId);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/by-start-date")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getLoanByStartDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        List<LoanEntity> loans = loansService.getByLoanStart(startDate);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/by-end-date")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getLoanByEndDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LoanEntity> loans = loansService.getByLoanEnd(endDate);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/before-date")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getLoanBeforeDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate) {
        List<LoanEntity> loans = loansService.getActiveBeforeDate(beforeDate);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> createLoan(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String mail = "";

            if (auth.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) auth.getPrincipal();
                mail = jwt.getClaimAsString("email");
            } else {
                mail = auth.getName();
            }

            Long workerId = workerClient.getWorkerByMail(mail).getWorkerId();

            Long clientId = Long.valueOf(request.get("clientId").toString());
            LocalDate startDate = LocalDate.parse(request.get("startDate").toString());
            LocalDate endDate = LocalDate.parse(request.get("endDate").toString());

            @SuppressWarnings("unchecked")
            List<Integer> toolIdsInt = (List<Integer>) request.get("toolIds");
            List<Long> toolIds = toolIdsInt.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            LoanEntity loan = loansService.createLoan(
                    clientId,
                    startDate,
                    endDate,
                    toolIds,
                    workerId);

            return ResponseEntity.status(HttpStatus.CREATED).body(loan);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el préstamo: " + e.getMessage()));
        }
    }

    @PostMapping("/{loanId}/return")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> returnLoan(@PathVariable Long loanId, @RequestBody Map<Long, String> unitCondition) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = "";

            if (authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                username = jwt.getClaimAsString("email");
            } else {
                username = authentication.getName();
            }

            Long workerId = workerClient.getWorkerByMail(username).getWorkerId();

            LoanEntity loan = loansService.returnLoan(loanId, workerId, unitCondition);
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/by-client-rut")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getLoansByClientRut(@RequestParam String rut) {
        List<LoanEntity> loans = loansService.getLoansByClientRut(rut);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/by-client-name")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getLoansByClientName(@RequestParam String name) {
        List<LoanEntity> loans = loansService.getLoansByClientName(name);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active-by-rut")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> getActiveLoansByClientRut(@RequestParam String rut) {
        List<LoanEntity> loans = loansService.getActiveLoansByClientRut(rut);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoanEntity>> searchLoan(
            @RequestParam(required = false) String rut,
            @RequestParam(required = false) String name) {
        if (name != null) {
            List<LoanEntity> loans = loansService.getLoansByClientName(name);
            return ResponseEntity.ok(loans);
        }
        if (rut != null) {
            List<LoanEntity> loans = loansService.getLoansByClientRut(rut);
            return ResponseEntity.ok(loans);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/clients-with-fine/{fine}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientDto>> getClientsWithFine(@PathVariable Long fine) {
        List<ClientDto> clients = loansService.getClientsWithFine(fine);
        return ResponseEntity.ok(clients);
    }
}
