package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.LoansEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoansRepository extends JpaRepository<LoansEntity, Long> {

    List<LoansEntity> findByClient_UserId(Long userId);

    List<LoansEntity> findByLoanStart(LocalDate loanStart);

    List<LoansEntity> findByActive(Boolean active);

    List<LoansEntity> findByLoanEnd(LocalDate loanEnd);

    List<LoansEntity> findByLoanEndBeforeAndActiveTrue(LocalDate date);

    long countByClient_UserIdAndActiveTrue(Long clientId);

}
