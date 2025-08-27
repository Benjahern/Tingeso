package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.LoanUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanUnitRepository extends JpaRepository<LoanUnitEntity, Long> {
}
