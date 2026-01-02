package com.example.kardex_service.Service;

import com.example.kardex_service.Entity.KardexEntity;
import com.example.kardex_service.Repository.KardexRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KardexService {

    private final KardexRepository kardexRepository;

    public KardexService(KardexRepository kardexRepository) {
        this.kardexRepository = kardexRepository;
    }

    @Transactional
    public void registerMovement(Long unitId, String movementType, Long workerId, Long loanId, String comment,
            Integer stockBalance, BigDecimal unitCost, BigDecimal totalValue) {

        Integer type = calculateType(movementType);

        KardexEntity kardexEntry = new KardexEntity();
        kardexEntry.setUnitId(unitId);
        kardexEntry.setMovement(movementType);
        kardexEntry.setCreatedAt(LocalDateTime.now());
        kardexEntry.setWorkerId(workerId);
        kardexEntry.setLoanId(loanId);
        kardexEntry.setType(type);
        kardexEntry.setStockBalance(stockBalance);
        kardexEntry.setUnitCost(unitCost);
        kardexEntry.setTotalValue(totalValue);
        kardexEntry.setComment(comment);

        kardexRepository.save(kardexEntry);
    }

    private Integer calculateType(String movementType) {
        return switch (movementType.toUpperCase()) {
            case "INGRESO_INVENTARIO", "ENTRADA_DEVOLUCION", "ENTRADA_COMPRA", "AJUSTE_POSITIVO" -> 1;
            case "SALIDA_PRESTAMO", "SALIDA_VENTA", "SALIDA_BAJA", "AJUSTE_NEGATIVO" -> -1;
            default -> 0;
        };
    }

    public List<KardexEntity> getHistoryForUnit(Long unitId) {
        return kardexRepository.findByUnitIdOrderByCreatedAtDesc(unitId);
    }

    public List<KardexEntity> getAllKardex() {
        return kardexRepository.findAll();
    }

    public List<KardexEntity> getHistoryByLoan(Long loanId) {
        return kardexRepository.findByLoanIdOrderByCreatedAtDesc(loanId);
    }

    public List<KardexEntity> getHistoryByWorker(Long workerId) {
        return kardexRepository.findByWorkerIdOrderByCreatedAtDesc(workerId);
    }

    public List<KardexEntity> getKardexByMovement(String movement) {
        return kardexRepository.findByMovementOrderByCreatedAtDesc(movement);
    }

    public List<KardexEntity> getKardexByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return kardexRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    public List<KardexEntity> getKardexByUnitAndMovement(Long unitId, String movement) {
        return kardexRepository.findByUnitIdAndMovementOrderByCreatedAtDesc(unitId, movement);
    }

    public long countMovementsByUnit(Long unitId) {
        return kardexRepository.countByUnitId(unitId);
    }

    @Transactional
    public void deleteByLoanId(Long loanId) {
        kardexRepository.deleteByLoanId(loanId);
    }


    // Método con fechas
    public List<Object[]> getRankingUnits(LocalDateTime startDate, LocalDateTime endDate){
        return kardexRepository.findMostRequestedUnitsByLoanInDateRange(startDate, endDate);
    }
}
