package com.example.Backend_ToolRent.controller;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class
LoanController {

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
    public ResponseEntity<List<LoansEntity>> getAllLoansByClientId(@PathVariable Long userId){
        List<LoansEntity> loans = loansService.getLoansByClientId(userId);
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
    public ResponseEntity<LoansEntity> createLoan(@RequestBody LoansEntity loan){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long workerId = workerService.getWorkerByName(username).getUserId();
        LoansEntity createdLoan = loansService.createLoan(loan, workerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    }

    @PostMapping("/{loanId}/return")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<LoansEntity> returnLoan(@PathVariable Long id, @RequestBody Map<Long, String> unitCondition){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long workerId = workerService.getWorkerByName(username).getUserId();

        LoansEntity loan = loansService.returnLoan(id,workerId,unitCondition);
        return ResponseEntity.ok(loan);
    }




}
