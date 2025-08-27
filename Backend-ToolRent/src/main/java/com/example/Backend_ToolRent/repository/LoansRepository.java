package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.LoansEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoansRepository extends JpaRepository<LoansEntity, Long> {
}
