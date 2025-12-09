package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.entity.UnitEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.ToolRepository;
import com.example.Backend_ToolRent.repository.UnitRepository;
import com.example.Backend_ToolRent.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnitService {

    private final UnitRepository unitRepo;
    private final WorkerRepository workerRepo;
    private final KardexService kardexService;
    private final ToolRepository toolRepo;

    public UnitService(UnitRepository unitRepo, ToolRepository toolRepo, WorkerRepository workerRepo, KardexService kardexService) {
        this.unitRepo = unitRepo;
        this.workerRepo = workerRepo;
        this.kardexService = kardexService;
        this.toolRepo = toolRepo;
    }

    @Transactional
    public UnitEntity createUnit(UnitEntity unit, Long workerId) {
        WorkerEntity workerEntity = workerRepo.findById(workerId).orElseThrow(() -> new EntityNotFoundException("Worker Not Found"));

        Long toolId = unit.getTool().getToolId();
        ToolEntity tool = toolRepo.findById(toolId).orElseThrow(() -> new EntityNotFoundException("Tool Not Found"));
        unit.setTool(tool);
        unit.setStatus("Disponible");

        UnitEntity newUnit = unitRepo.save(unit);

        kardexService.registerMovement(newUnit, "INGRESO_INVENTARIO", workerEntity, null, "Registro de unidad en nel sistema. Serie: " + newUnit.getUnitId());

        return newUnit;
    }

    public UnitEntity findUnitById(Long unitId) {
        return unitRepo.findById(unitId).orElseThrow(() -> new EntityNotFoundException("Unit Not Found"));
    }

    @Transactional
    public UnitEntity updateUnit(Long unitId, UnitEntity unit, Long workerId) {

        WorkerEntity worker = workerRepo.findById(workerId).orElseThrow(() -> new EntityNotFoundException("Worker Not Found"));
        UnitEntity oldUnit = findUnitById(unitId);
        String oldStatus = oldUnit.getStatus();
        String oldCondition = oldUnit.getCondition();

        oldUnit.setStatus(unit.getStatus());
        oldUnit.setCondition(unit.getCondition());

        UnitEntity updatedUnit = unitRepo.save(oldUnit);

        if (!oldStatus.equals(unit.getStatus()) || !oldCondition.equals(unit.getCondition())) {
            String comment = String.format(
                    "Actualización de unidad. Estado: %s -> %s, Condición: %s -> %s",
                    oldStatus, unit.getStatus(), oldCondition, unit.getCondition()
            );

            kardexService.registerMovement(
                    updatedUnit,
                    "AJUSTE_ESTADO",
                    worker,
                    null,
                    comment
            );
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

    



}
