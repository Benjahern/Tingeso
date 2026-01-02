package com.example.loans_service.Repository;

import com.example.loans_service.Entity.LoanUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanUnitRepository extends JpaRepository<LoanUnitEntity, Long> {
    List<LoanUnitEntity> findByUnitId(Long unitId);

    List<LoanUnitEntity> findByState(String state);
}
