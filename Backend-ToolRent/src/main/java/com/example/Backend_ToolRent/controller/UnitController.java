package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.LoansEntity;
import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.entity.UnitEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.ClientRepository;
import com.example.Backend_ToolRent.repository.ToolRepository;
import com.example.Backend_ToolRent.repository.UnitRepository;
import com.example.Backend_ToolRent.service.KardexService;
import com.example.Backend_ToolRent.service.UnitService;
import com.example.Backend_ToolRent.service.WorkerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/units")
@CrossOrigin("*")
public class UnitController {

    private final UnitService unitService;
    private final WorkerService workerService;
    private final KardexService kardexService;

    @Autowired
    private UnitRepository unitRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ToolRepository toolRepo;


    public UnitController(UnitService unitService, WorkerService workerService, KardexService kardexService) {
        this.unitService = unitService;
        this.kardexService = kardexService;
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> updateUnit(@PathVariable Long id, @RequestBody UnitEntity unitDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if(auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            username = jwt.getClaimAsString("email");

        }
        Long workerId = workerService.getWorkerByMail(username).getUserId();
        UnitEntity updatedUnit = unitService.updateUnit(id, unitDetails, workerId);
        return ResponseEntity.ok(updatedUnit);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> createUnit(@RequestBody UnitEntity unit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if(authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            username = jwt.getClaimAsString("email");

        }
        if(username == null) {
            throw new RuntimeException("No se pudo extraer el username del token");
        }
        Long workerId = workerService.getWorkerByMail(username).getUserId();

        UnitEntity newUnit = unitService.createUnit(unit, workerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUnit);
    }

    @PostMapping("/{unitId}/decommission")
    public ResponseEntity<?> decommissionUnit(@PathVariable Long unitId, @RequestBody Map<String, String> data, @AuthenticationPrincipal Jwt jwt) {
        UnitEntity unit = unitService.findUnitById(unitId);
        WorkerEntity worker = workerService.getWorkerByMail(jwt.getClaimAsString("preferred_username"));

        ToolEntity tool = unit.getTool();

        unit.setStatus("Dado de Baja");
        unit.setCondition(data.get("condition"));
        unitRepo.save(unit);

        kardexService.registerMovement(
                unit,
                "SALIDA_BAJA",
                worker,
                null,
                data.getOrDefault("comment", "Unidad dada de baja por daño")
        );

        tool.setStock(tool.getStock() -1 );
        toolRepo.save(tool);

        return ResponseEntity.ok("Unit decommissioned successfully");
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<UnitEntity> updateUnitStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if(auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            username = jwt.getClaimAsString("email");
        }

        Long workerId = workerService.getWorkerByMail(username).getUserId();

        UnitEntity unit = unitService.findUnitById(id);
        unit.setStatus(statusUpdate.get("status"));
        unit.setCondition(statusUpdate.get("condition"));

        UnitEntity updatedUnit = unitService.updateUnit(id, unit, workerId);

        return ResponseEntity.ok(updatedUnit);
    }

}
