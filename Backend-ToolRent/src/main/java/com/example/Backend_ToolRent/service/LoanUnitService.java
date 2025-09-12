package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.LoanUnitEntity;
import com.example.Backend_ToolRent.repository.LoanUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanUnitService {

    private final LoanUnitRepository loanUnitRepo;

    public LoanUnitService(LoanUnitRepository loanUnitRepository) {
        this.loanUnitRepo = loanUnitRepository;
    }

    public LoanUnitEntity getLoanUnitById(Long id) {
        return loanUnitRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LoanUnit no encontrado con ID: " + id));
    }

    public List<LoanUnitEntity> getHistoryForUnit(Long unitId) {
        return loanUnitRepo.findByUnit_UnitId(unitId);
    }


    public List<LoanUnitEntity> findReturnsByState(String state) {
        return loanUnitRepo.findByState(state);
    }
}