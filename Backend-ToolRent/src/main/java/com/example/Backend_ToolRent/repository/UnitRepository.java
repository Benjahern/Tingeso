package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Long> {
}
