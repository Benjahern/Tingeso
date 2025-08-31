package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.ClientEntity;
import com.example.Backend_ToolRent.model.WorkerEntity;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, Long> {

    Optional<WorkerEntity> findByUserNameContainingIgnoreCase(String userName);

    List<WorkerEntity> findByStore_StoreId(Long storeId);


}
