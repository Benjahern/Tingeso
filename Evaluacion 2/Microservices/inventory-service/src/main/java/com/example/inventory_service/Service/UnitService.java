package com.example.inventory_service.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventory_service.Client.KardexClient;
import com.example.inventory_service.Client.WorkerClient;
import com.example.inventory_service.Entity.ToolEntity;
import com.example.inventory_service.Entity.UnitEntity;
import com.example.inventory_service.Models.KardexDto;
import com.example.inventory_service.Models.WorkerDto;
import com.example.inventory_service.Repository.ToolRepository;
import com.example.inventory_service.Repository.UnitRepository;

import java.util.List;

@Service
public class UnitService {

    private final UnitRepository unitRepo;
    private final WorkerClient workerClient;
    private final KardexClient kardexClient;
    private final ToolRepository toolRepo;

    public UnitService(UnitRepository unitRepo, WorkerClient workerClient, KardexClient kardexClient,
            ToolRepository toolRepo) {
        this.unitRepo = unitRepo;
        this.workerClient = workerClient;
        this.kardexClient = kardexClient;
        this.toolRepo = toolRepo;
    }

    @Transactional
    public UnitEntity createUnit(UnitEntity unit, Long workerId) {
        // Validar que el worker existe (si no existe, Feign lanzará una excepción)
        WorkerDto worker = workerClient.getWorker(workerId);
        if (worker == null) {
            throw new EntityNotFoundException("Worker Not Found with id: " + workerId);
        }

        Long toolId = unit.getTool().getToolId();
        ToolEntity tool = toolRepo.findById(toolId).orElseThrow(() -> new EntityNotFoundException("Tool Not Found"));
        System.out.println(
                "UnitService: creating unit for tool " + toolId + ". Tool Stock from DB is: " + tool.getStock());

        // Update Stock
        tool.setStock(tool.getStock() + 1);
        toolRepo.save(tool);

        unit.setTool(tool);
        unit.setStatus("Disponible");

        UnitEntity newUnit = unitRepo.save(unit);

        KardexDto kardexDto = new KardexDto();
        kardexDto.setUnitId(newUnit.getUnitId());
        kardexDto.setWorkerId(workerId);
        kardexDto.setMovement("INGRESO_INVENTARIO");
        kardexDto.setLoanId(null);
        kardexDto.setStockBalance(tool.getStock());
        kardexDto.setComment("Registro de unidad en el sistema. Serie: " + newUnit.getUnitId());
        kardexDto.setType(1);
        kardexClient.createMovement(kardexDto);

        return newUnit;
    }

    public UnitEntity findUnitById(Long unitId) {
        return unitRepo.findById(unitId).orElseThrow(() -> new EntityNotFoundException("Unit Not Found"));
    }

    @Transactional
    public UnitEntity updateUnit(Long unitId, UnitEntity unit, Long workerId) {

        WorkerDto worker = workerClient.getWorker(workerId);
        if (worker == null) {
            throw new EntityNotFoundException("Worker Not Found with id: " + workerId);
        }

        UnitEntity oldUnit = findUnitById(unitId);
        String oldStatus = oldUnit.getStatus();
        String oldCondition = oldUnit.getCondition();

        // Update parcial: solo actualizar si el valor NO es null
        if (unit.getStatus() != null) {
            oldUnit.setStatus(unit.getStatus());
        }
        if (unit.getCondition() != null) {
            oldUnit.setCondition(unit.getCondition());
        }

        UnitEntity updatedUnit = unitRepo.save(oldUnit);

        // Verificar si hubo cambios reales para registrar en kardex
        boolean statusChanged = unit.getStatus() != null && !oldStatus.equals(unit.getStatus());
        boolean conditionChanged = unit.getCondition() != null && !oldCondition.equals(unit.getCondition());

        if (statusChanged || conditionChanged) {
            String comment = String.format(
                    "Actualización de unidad. Estado: %s -> %s, Condición: %s -> %s",
                    oldStatus, updatedUnit.getStatus(), oldCondition, updatedUnit.getCondition());
            KardexDto kardexDto = new KardexDto();
            kardexDto.setUnitId(updatedUnit.getUnitId());
            kardexDto.setComment(comment);
            kardexDto.setType(0);
            kardexDto.setWorkerId(workerId);
            kardexDto.setMovement("AJUSTE_ESTADO");
            kardexDto.setLoanId(null);
            kardexDto.setStockBalance(updatedUnit.getTool().getStock());

            kardexClient.createMovement(kardexDto);
        }

        return updatedUnit;
    }

    public List<UnitEntity> findAllUnit() {
        return unitRepo.findAll();
    }

    public List<UnitEntity> getUnitByToolID(Long toolId) {
        return unitRepo.findByTool_ToolId(toolId);
    }

    public List<UnitEntity> getUnitByStatus(String status) {
        return unitRepo.findByStatus(status);
    }

    public List<UnitEntity> getUnitByCondition(String condition) {
        return unitRepo.findByCondition(condition);
    }

    public List<UnitEntity> getUnitByName(String unitName) {
        return unitRepo.findByTool_ToolNameContainingIgnoreCase(unitName);
    }

    public UnitEntity findFirstAvailableByToolId(Long toolId) {
        return unitRepo.findFirstByTool_ToolIdAndStatusAndConditionNot(
                toolId, "Disponible", "Dañado")
                .orElseThrow(() -> new EntityNotFoundException(
                        "No hay unidades disponibles para la herramienta ID: " + toolId));
    }

}
