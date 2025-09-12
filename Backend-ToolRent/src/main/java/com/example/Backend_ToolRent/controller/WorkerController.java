package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.service.WorkerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin("*")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<WorkerEntity> getWorker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WorkerEntity worker = workerService.getWorkerByName(username);
        return ResponseEntity.ok(worker);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<WorkerEntity>> getAllWorkers() {
        List<WorkerEntity> workers = workerService.getAllWorkers();
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<WorkerEntity> getWorkerById(@PathVariable Long id) {
        WorkerEntity worker = workerService.getWorkerById(id);
        return ResponseEntity.ok(worker);
    }

    @GetMapping("/by-store/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<WorkerEntity>> getWorkersByStore(@PathVariable Long storeId) {
        List<WorkerEntity> workers = workerService.getWorkerByStore(storeId);
        return ResponseEntity.ok(workers);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerEntity> addWorker(@RequestBody WorkerEntity worker) {
        WorkerEntity newWorker = workerService.addWorker(worker);
        return ResponseEntity.status(HttpStatus.CREATED).body(newWorker);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerEntity> updateWorker(@PathVariable Long id, @RequestBody WorkerEntity workerDetails) {
        // Tu servicio ya tiene un método 'updateWorker', pero idealmente debería recibir el ID.
        // Por ahora, nos aseguramos de que el ID sea consistente.
        workerDetails.setUserId(id);
        WorkerEntity updatedWorker = workerService.updateWorker(workerDetails);
        return ResponseEntity.ok(updatedWorker);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build(); // Respuesta 204 No Content
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RolEntity>> getRolesByWorker(@PathVariable Long id) {
        List<RolEntity> roles = workerService.getRolByWorkerId(id);
        return ResponseEntity.ok(roles);
    }

}
