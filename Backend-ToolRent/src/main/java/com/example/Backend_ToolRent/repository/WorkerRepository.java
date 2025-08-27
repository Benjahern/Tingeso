package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, Long> {
}
