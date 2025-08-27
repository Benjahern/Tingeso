package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {
}
