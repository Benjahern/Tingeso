package com.example.kardex_service.Repository;

import com.example.kardex_service.Entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {

        // Buscar por unitId (ahora es un Long simple)
        List<KardexEntity> findByUnitIdOrderByCreatedAtDesc(Long unitId);

        // Buscar por rango de fechas
        List<KardexEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

        // Buscar por loanId (ahora es un Long simple)
        List<KardexEntity> findByLoanIdOrderByCreatedAtDesc(Long loanId);

        // Eliminar por loanId
        void deleteByLoanId(Long loanId);

        // Buscar por workerId
        List<KardexEntity> findByWorkerIdOrderByCreatedAtDesc(Long workerId);

        // Buscar por tipo de movimiento
        List<KardexEntity> findByMovementOrderByCreatedAtDesc(String movement);

        // Contar movimientos por unitId
        long countByUnitId(Long unitId);

        // Buscar por unitId y tipo de movimiento
        List<KardexEntity> findByUnitIdAndMovementOrderByCreatedAtDesc(Long unitId, String movement);

        /**
         * Ranking de unidades más solicitadas por préstamo en un rango de fechas.
         * Devuelve una lista de Object[] donde:
         * - Object[0] = unitId (Long)
         * - Object[1] = totalSolicitudes (Long)
         * 
         * Nota: En microservicios, el reports-service debe luego llamar a
         * inventory-service para obtener la información del Tool asociado a cada
         * unitId.
         */
        @Query("SELECT k.unitId, COUNT(k) as totalSolicitudes " +
                        "FROM KardexEntity k " +
                        "WHERE k.movement = 'SALIDA_PRESTAMO' " +
                        "AND k.loanId IS NOT NULL " +
                        "AND k.createdAt >= :startDate " +
                        "AND k.createdAt <= :endDate " +
                        "GROUP BY k.unitId " +
                        "ORDER BY totalSolicitudes DESC")
        List<Object[]> findMostRequestedUnitsByLoanInDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
