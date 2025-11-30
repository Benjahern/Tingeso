package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.ClientEntity;
import com.example.Backend_ToolRent.entity.LoansEntity;
import com.example.Backend_ToolRent.service.LoansService;
import com.example.Backend_ToolRent.service.WorkerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin("*")
public class LoanController {

    private final LoansService loansService;
    private final WorkerService workerService;

    public LoanController(LoansService loansService, WorkerService workerService) {
        this.loansService = loansService;
        this.workerService = workerService;
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<LoansEntity> getLoanById(@PathVariable Long id) {
        LoansEntity loan = loansService.getLoansById(id);
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getActiveLoans() {
        List<LoansEntity> activeLoans = loansService.getLoansByActive();
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
    public ResponseEntity<List<LoansEntity>> getInactiveLoans(){
        List<LoansEntity> inactiveLoans = loansService.getLoansByInactive();
        return new ResponseEntity<>(inactiveLoans, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getAllLoans(){
        List<LoansEntity> allLoans = loansService.getAllLoans();
        return new ResponseEntity<>(allLoans, HttpStatus.OK);
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getAllLoansByClientId(@PathVariable Long clientId){
        List<LoansEntity> loans = loansService.getLoansByClientId(clientId);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/by-start-date")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getLoanByStartDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate){
        List<LoansEntity> loans = loansService.getByLoanStart(startDate);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/by-end-date")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getLoanByEndDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LoansEntity> loans = loansService.getByLoanEnd(endDate);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/before-date")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getLoanBeforeDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate){
        List<LoansEntity> loans = loansService.getActiveBeforeDate(beforeDate);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> createLoan(@RequestBody Map<String, Object> request){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String mail = "";

            // --- CORRECCIÓN: Extraer email del Token JWT ---
            if (auth.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) auth.getPrincipal();
                mail = jwt.getClaimAsString("email"); // Obtenemos "benja@gmail.com"
            } else {
                mail = auth.getName(); // Fallback (devuelve el ID UUID, causará error si no es JWT)
            }
            // -----------------------------------------------

            // Ahora sí encontrará al trabajador por su email real
            Long workerId = workerService.getWorkerByMail(mail).getUserId();

            Long clientId = Long.valueOf(request.get("clientId").toString());
            Long storeId = Long.valueOf(request.get("storeId").toString());
            LocalDate startDate = LocalDate.parse(request.get("startDate").toString());
            LocalDate endDate = LocalDate.parse(request.get("endDate").toString());

            @SuppressWarnings("unchecked")
            List<Integer> toolIdsInt = (List<Integer>) request.get("toolIds");
            List<Long> toolIds = toolIdsInt.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            LoansEntity loan = loansService.createLoan(
                    clientId,
                    storeId,
                    startDate,
                    endDate,
                    toolIds,
                    workerId
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(loan);

        } catch (Exception e){
            e.printStackTrace(); // Útil para ver errores en logs de docker
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al crear el préstamo: " + e.getMessage()));
        }
    }

    @PostMapping("/{loanId}/return")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> returnLoan(@PathVariable Long loanId, @RequestBody Map<Long, String> unitCondition){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = "";

            // --- CORRECCIÓN: Extraer email del Token JWT ---
            if (authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                username = jwt.getClaimAsString("email");
            } else {
                username = authentication.getName();
            }
            // -----------------------------------------------

            Long workerId = workerService.getWorkerByMail(username).getUserId();

            LoansEntity loan = loansService.returnLoan(loanId, workerId, unitCondition);
            return ResponseEntity.ok(loan);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/by-client-name")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getLoansByClientName(@RequestParam String name) {
        List<LoansEntity> loans = loansService.getLoansByClientName(name);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/by-client-rut")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getLoansByClientRut(@RequestParam String rut) {
        List<LoansEntity> loans = loansService.getLoansByClientRut(rut);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active-by-rut")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> getActiveLoansByClientRut(@RequestParam String rut) {
        List<LoansEntity> loans = loansService.getActiveLoansByClientRut(rut);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<LoansEntity>> searchLoan(@RequestParam(required = false) String rut, @RequestParam(required = false) String name){
        if(name != null){
            List<LoansEntity> loans = loansService.getLoansByClientName(name);
            return ResponseEntity.ok(loans);
        }
        if(rut != null){
            List<LoansEntity> loans = loansService.getLoansByClientRut(rut);
            return ResponseEntity.ok(loans);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/clientfine/{fine}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientEntity>> getClientsWithFine(@PathVariable Long fine){
        List<ClientEntity> clients = loansService.getClientsWithFine(fine);
        return ResponseEntity.ok(clients);
    }
}
