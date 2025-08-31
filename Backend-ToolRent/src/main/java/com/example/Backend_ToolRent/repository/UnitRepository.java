package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Long> {

    List<UnitEntity> findByTool_ToolId(Long toolId);

    List<UnitEntity> findByStatus(String status);

    List<UnitEntity> findByCondition(String condition);

    List<UnitEntity> findByTool_ToolNameContainingIgnoreCase(String toolName);
}
