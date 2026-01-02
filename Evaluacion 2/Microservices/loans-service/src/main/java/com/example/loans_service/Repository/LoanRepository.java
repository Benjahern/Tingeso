package com.example.loans_service.Repository;

import com.example.loans_service.Entity.LoanEntity;
import com.example.loans_service.Entity.LoanUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    List<LoanEntity> findByClientId(Long clientId);

    List<LoanEntity> findByClientIdAndActiveTrue(Long clientId);

    List<LoanEntity> findByLoanStart(LocalDate loanStart);

    List<LoanEntity> findByActive(Boolean active);

    List<LoanEntity> findByLoanEnd(LocalDate loanEnd);

    List<LoanEntity> findByLoanEndBeforeAndActiveTrue(LocalDate date);

    long countByClientIdAndActiveTrue(Long clientId);

    List<LoanEntity> findByFineIsGreaterThan(Long fine);

}
