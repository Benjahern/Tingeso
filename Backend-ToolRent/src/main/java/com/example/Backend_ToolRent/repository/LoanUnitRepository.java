package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.LoanUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanUnitRepository extends JpaRepository<LoanUnitEntity, Long> {

    List<LoanUnitEntity> findByUnit_UnitId(Long unitId);

    List<LoanUnitEntity> findByState(String state);


}
