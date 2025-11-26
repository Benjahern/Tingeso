package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.ClientEntity;
import com.example.Backend_ToolRent.entity.LoansEntity;
import com.example.Backend_ToolRent.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoansRepository extends JpaRepository<LoansEntity, Long> {

    List<LoansEntity> findByClient_UserId(Long userId);

    List<LoansEntity> findByClient_UserIdAndActiveTrue(Long clientId);

    List<LoansEntity> findByClient_NameContainingIgnoreCase(String name);

    List<LoansEntity> findByClient_RutContainingIgnoreCase(String rut);

    List<LoansEntity> findByClient_RutAndActiveTrue(String rut);

    List<LoansEntity> findByLoanStart(LocalDate loanStart);

    List<LoansEntity> findByActive(Boolean active);

    List<LoansEntity> findByLoanEnd(LocalDate loanEnd);

    List<LoansEntity> findByLoanEndBeforeAndActiveTrue(LocalDate date);

    long countByClient_UserIdAndActiveTrue(Long clientId);

    List<LoansEntity> findByFineIsGreaterThan(Long fine);



}
