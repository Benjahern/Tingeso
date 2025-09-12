package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.UnitEntity;
import com.example.Backend_ToolRent.service.UnitService;
import com.example.Backend_ToolRent.service.WorkerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@CrossOrigin("*")
public class UnitController {

    private final UnitService unitService;
    private final WorkerService workerService;

    public UnitController(UnitService unitService, WorkerService workerService) {
        this.unitService = unitService;
        this.workerService = workerService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<UnitEntity>> getAllUnits() {
        List<UnitEntity> units = unitService.findAllUnit();
        return ResponseEntity.ok(units);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> getUnitById(@PathVariable Long id) {
        UnitEntity unit = unitService.findUnitById(id);
        return ResponseEntity.ok(unit);
    }

    @GetMapping("/by-tool/{toolId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<UnitEntity>> getUnitsByToolId(@PathVariable Long toolId) {
        List<UnitEntity> units = unitService.getUnitByToolID(toolId);
        return ResponseEntity.ok(units);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<UnitEntity>> searchUnits(@RequestParam(required = false) String status, @RequestParam(required = false) String condition, @RequestParam(required = false) String toolName) {
        if (status != null) {
            return ResponseEntity.ok(unitService.getUnitByStatus(status));
        }
        if (condition != null) {
            return ResponseEntity.ok(unitService.getUnitByCondition(condition));
        }
        if (toolName != null) {
            return ResponseEntity.ok(unitService.getUnitByName(toolName));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnitEntity> createUnit(@RequestBody UnitEntity unit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long workerId = workerService.getWorkerByName(username).getUserId();

        UnitEntity newUnit = unitService.createUnit(unit, workerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUnit);
    }

}
