package com.example.users_service.Repository;

import com.example.users_service.Entity.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, Long> {

    Optional<WorkerEntity> findByNameIgnoreCase(String name);

    Optional<WorkerEntity> findByMail(String mail);

}
