package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {

    List<KardexEntity> findByUnit_UnitIdOrderByDateDesc(Long unitId);

    List<KardexEntity> findByUnit_Tool_ToolIdOrderByDateDesc(Long toolId);

    List<KardexEntity> findByDateBetweenOrderByDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<KardexEntity> findByStore_StoreIdOrderByDateDesc(Long storeId);

    void deleteByLoan_LoanId(Long loanLoanId);

    @Query("SELECT k.unit.tool, COUNT(k) as totalSolicitudes " +
            "FROM KardexEntity k " +
            "WHERE k.movement = 'Salida_Prestamo' " +
            "AND k.loan IS NOT NULL " +
            "GROUP BY k.unit.tool " +
            "ORDER BY totalSolicitudes DESC")
    List<Object[]> findMostRequestedToolsWithLoan();

    // Método con filtro de fechas
    @Query("SELECT k.unit.tool, COUNT(k) as totalSolicitudes " +
            "FROM KardexEntity k " +
            "WHERE k.movement = 'Salida_Prestamo' " +
            "AND k.loan IS NOT NULL " +
            "AND k.date >= :startDate " +
            "AND k.date <= :endDate " +
            "GROUP BY k.unit.tool " +
            "ORDER BY totalSolicitudes DESC")
    List<Object[]> findMostRequestedToolsWithLoanByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

