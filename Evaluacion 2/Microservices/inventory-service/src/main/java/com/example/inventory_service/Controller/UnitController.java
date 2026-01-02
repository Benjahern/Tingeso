package com.example.inventory_service.Controller;

import com.example.inventory_service.Client.KardexClient;
import com.example.inventory_service.Client.WorkerClient;
import com.example.inventory_service.Entity.ToolEntity;
import com.example.inventory_service.Entity.UnitEntity;
import com.example.inventory_service.Models.KardexDto;
import com.example.inventory_service.Models.WorkerDto;
import com.example.inventory_service.Repository.ToolRepository;
import com.example.inventory_service.Repository.UnitRepository;
import com.example.inventory_service.Service.UnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/units")
public class UnitController {

    private final UnitService unitService;
    private final WorkerClient workerClient;
    private final KardexClient kardexClient;
    private final ToolRepository toolRepo;
    private final UnitRepository unitRepo;

    public UnitController(UnitService unitService, WorkerClient workerClient, UnitRepository unitRepo,
            KardexClient kardexClient, ToolRepository toolRepo) {
        this.unitService = unitService;
        this.toolRepo = toolRepo;
        this.unitRepo = unitRepo;
        this.workerClient = workerClient;
        this.kardexClient = kardexClient;
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
    public ResponseEntity<List<UnitEntity>> searchUnits(@RequestParam(required = false) String status,
            @RequestParam(required = false) String condition, @RequestParam(required = false) String toolName) {
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> updateUnit(@PathVariable Long id, @RequestBody UnitEntity unitDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            username = jwt.getClaimAsString("email");

        }
        Long workerId = workerClient.getWorkerByMail(username).getWorkerId();
        UnitEntity updatedUnit = unitService.updateUnit(id, unitDetails, workerId);
        return ResponseEntity.ok(updatedUnit);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> createUnit(@RequestBody UnitEntity unit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            username = jwt.getClaimAsString("email");

        }
        if (username == null) {
            throw new RuntimeException("No se pudo extraer el username del token");
        }
        Long workerId = workerClient.getWorkerByMail(username).getWorkerId();

        UnitEntity newUnit = unitService.createUnit(unit, workerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUnit);
    }

    @PostMapping("/{unitId}/decommission")
    public ResponseEntity<?> decommissionUnit(@PathVariable Long unitId, @RequestBody Map<String, String> data,
            @AuthenticationPrincipal Jwt jwt) {
        UnitEntity unit = unitService.findUnitById(unitId);
        WorkerDto worker = workerClient.getWorkerByMail(jwt.getClaimAsString("email"));

        ToolEntity tool = unit.getTool();

        unit.setStatus("Dado de Baja");
        unit.setCondition(data.get("condition"));
        unitRepo.save(unit);

        KardexDto kardex = new KardexDto();
        kardex.setUnitId(unitId);
        kardex.setWorkerId(worker.getWorkerId());
        kardex.setMovement("SALIDA_BAJA");
        kardex.setLoanId(null);
        kardex.setComment("Unidad dada de baja por daño");

        kardexClient.createMovement(kardex);

        tool.setStock(tool.getStock() - 1);
        toolRepo.save(tool);

        return ResponseEntity.ok("Unit decommissioned successfully");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> updateUnitStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            username = jwt.getClaimAsString("email");
        }

        Long workerId = workerClient.getWorkerByMail(username).getWorkerId();

        UnitEntity unit = unitService.findUnitById(id);
        unit.setStatus(statusUpdate.get("status"));
        unit.setCondition(statusUpdate.get("condition"));

        UnitEntity updatedUnit = unitService.updateUnit(id, unit, workerId);

        return ResponseEntity.ok(updatedUnit);
    }

    @GetMapping("/available/by-tool/{toolId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> getFirstAvailableByToolId(@PathVariable Long toolId) {
        UnitEntity unit = unitService.findFirstAvailableByToolId(toolId);
        return ResponseEntity.ok(unit);
    }

}
