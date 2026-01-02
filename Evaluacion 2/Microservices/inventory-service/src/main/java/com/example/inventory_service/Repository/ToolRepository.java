package com.example.inventory_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventory_service.Entity.ToolEntity;

import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {

    Optional<ToolEntity> findByToolNameContainingIgnoreCase(String toolName);
}
