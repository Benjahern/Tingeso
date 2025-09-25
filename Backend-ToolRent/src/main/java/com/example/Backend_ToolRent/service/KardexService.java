package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.*;
import com.example.Backend_ToolRent.repository.KardexRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KardexService {

    private final KardexRepository kardexRepository;

    public KardexService(KardexRepository kardexRepository) {
        this.kardexRepository = kardexRepository;
    }

    public void registerMovement(UnitEntity unit, String movementType, WorkerEntity worker, LoansEntity loan, String comment) {
        KardexEntity kardexEntry = new KardexEntity();

        kardexEntry.setUnit(unit);
        kardexEntry.setMovement(movementType);
        kardexEntry.setDate(LocalDateTime.now());
        kardexEntry.setWorker(worker);
        kardexEntry.setStore(worker.getStore());
        kardexEntry.setLoan(loan);
        kardexEntry.setComment(comment);

        kardexRepository.save(kardexEntry);
    }

    public List<KardexEntity> getHistoryForUnit(Long unitId){
        return kardexRepository.findByUnit_UnitIdOrderByDateDesc(unitId);
    }

    public List<KardexEntity> getAllKardex(){
        return kardexRepository.findAll();
    }

    public List<KardexEntity> getHistoryForTool(Long toolId){
        return kardexRepository.findByUnit_Tool_ToolIdOrderByDateDesc(toolId);
    }
}