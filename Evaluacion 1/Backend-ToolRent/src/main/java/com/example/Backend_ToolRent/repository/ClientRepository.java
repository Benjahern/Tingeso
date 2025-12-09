package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    List<ClientEntity> findByNameContainingIgnoreCase(String userName);

    List<ClientEntity> findByDebtGreaterThan(double debt);

    List<ClientEntity> findByState(String state);

    List<ClientEntity> findByRut(String rut);
}
