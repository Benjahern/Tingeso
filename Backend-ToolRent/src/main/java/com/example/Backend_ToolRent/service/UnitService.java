package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.UnitEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
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

    public UnitService(UnitRepository unitRepo, WorkerRepository workerRepo, KardexService kardexService) {
        this.unitRepo = unitRepo;
        this.workerRepo = workerRepo;
        this.kardexService = kardexService;
    }

    @Transactional
    public UnitEntity createUnit(UnitEntity unit, Long workerId) {
        WorkerEntity workerEntity = workerRepo.findById(workerId).orElseThrow(() -> new EntityNotFoundException("Worker Not Found"));

        unit.setStatus("Disponible");
        unit.setCondition("Bueno");

        UnitEntity newUnit = unitRepo.save(unit);

        kardexService.registerMovement(newUnit, "INGRESO_INVENTARIO", workerEntity, null, "Registro de unidad en nel sistema.");

        return newUnit;
    }

    public UnitEntity findUnitById(Long unitId) {
        return unitRepo.findById(unitId).orElseThrow(() -> new EntityNotFoundException("Unit Not Found"));
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
