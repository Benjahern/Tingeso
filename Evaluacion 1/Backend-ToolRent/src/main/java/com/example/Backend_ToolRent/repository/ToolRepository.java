package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {

    Optional<ToolEntity> findByToolNameContainingIgnoreCase(String toolName);
}
