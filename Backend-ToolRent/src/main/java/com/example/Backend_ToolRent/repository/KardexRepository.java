package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {

    List<KardexEntity> findByUnit_UnitIdOrderByDateDesc(Long unitId);

    List<KardexEntity> findByUnit_Tool_ToolIdOrderByDateDesc(Long toolId);

}
