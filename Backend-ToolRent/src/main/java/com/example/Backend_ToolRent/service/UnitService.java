package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.model.UnitEntity;
import com.example.Backend_ToolRent.repository.UnitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService {

    private final UnitRepository unitRepo;

    public UnitService(UnitRepository unitRepo) {
        this.unitRepo = unitRepo;
    }

    public UnitEntity saveUnit(UnitEntity unitEntity) {
        return unitRepo.save(unitEntity);
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
