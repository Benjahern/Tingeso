package com.example.loans_service.Service;

import com.example.loans_service.Entity.LoanUnitEntity;
import com.example.loans_service.Repository.LoanUnitRepository;
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

    public LoanUnitEntity createLoanUnit(LoanUnitEntity loanUnitEntity) {
        return loanUnitRepo.save(loanUnitEntity);
    }

    public List<LoanUnitEntity> getHistoryForUnit(Long unitId) {
        return loanUnitRepo.findByUnitId(unitId);
    }


    public List<LoanUnitEntity> findReturnsByState(String state) {
        return loanUnitRepo.findByState(state);
    }
}
