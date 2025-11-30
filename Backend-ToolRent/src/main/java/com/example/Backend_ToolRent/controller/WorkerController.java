package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.entity.StoreEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.RolRepository;
import com.example.Backend_ToolRent.repository.StoreRepository;
import com.example.Backend_ToolRent.service.RolService;
import com.example.Backend_ToolRent.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin("*")
public class WorkerController {

    private final WorkerService workerService;
    private final RolService rolService;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private StoreRepository storeRepo;

    public WorkerController(WorkerService workerService, RolService rolService) {
        this.workerService = workerService;
        this.rolService = rolService;
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
    public ResponseEntity<WorkerEntity> addWorker(@RequestBody Map<String, Object> request) {
        WorkerEntity worker = new WorkerEntity();
        worker.setName((String) request.get("name"));
        worker.setMail((String) request.get("mail"));

        // Obtener roles por IDs
        List<Long> roleIds = (List<Long>) request.get("roleIds");
        List<RolEntity> roles = rolRepo.findAllById(roleIds);
        worker.setRol(new HashSet<>(worker.getRol()));
        // Obtener store
        Long storeId = ((Number) request.get("storeId")).longValue();
        StoreEntity store = storeRepo.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        worker.setStore(store);

        String password = (String) request.get("password");

        WorkerEntity created = workerService.addWorker(worker, password);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerEntity> updateWorker(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        WorkerEntity worker = new WorkerEntity();
        worker.setName((String) request.get("name"));
        worker.setMail((String) request.get("mail"));

        // Obtener roles por IDs
        List<Long> roleIds = (List<Long>) request.get("roleIds");
        List<RolEntity> roles = rolRepo.findAllById(roleIds);
        worker.setRol(new HashSet<>(worker.getRol()));

        WorkerEntity updated = workerService.updateWorker(id, worker);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RolEntity>> getRolesByWorker(@PathVariable Long id) {
        List<RolEntity> roles = workerService.getRolByWorkerId(id);
        return ResponseEntity.ok(roles);
    }

}
