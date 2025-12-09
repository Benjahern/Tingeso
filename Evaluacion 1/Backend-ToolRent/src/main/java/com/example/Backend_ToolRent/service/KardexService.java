package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.*;
import com.example.Backend_ToolRent.repository.KardexRepository;
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
    public void registerMovement(UnitEntity unit, String movementType, WorkerEntity worker, LoansEntity loan, String comment) {

        Integer type = calculateType(movementType);

        ToolEntity tool = unit.getTool();
        Integer currentStock = tool.getStock();

        Integer stockBalance = currentStock;

        BigDecimal unitCost = tool.getReplacementValue() != null
                ? BigDecimal.valueOf(tool.getReplacementValue())
                : BigDecimal.ZERO;

        BigDecimal totalValue = unitCost.multiply(BigDecimal.valueOf(Math.abs(type)));

        KardexEntity kardexEntry = new KardexEntity();
        kardexEntry.setUnit(unit);
        kardexEntry.setMovement(movementType);
        kardexEntry.setDate(LocalDateTime.now());
        kardexEntry.setWorker(worker);
        kardexEntry.setStore(worker.getStore());
        kardexEntry.setLoan(loan);
        kardexEntry.setType(type);
        kardexEntry.setStockBalance(stockBalance);
        kardexEntry.setUnitCost(unitCost);
        kardexEntry.setTotalValue(totalValue);
        kardexEntry.setComment(comment);

        kardexRepository.save(kardexEntry);
    }

    private Integer calculateType(String movementType) {
        return switch (movementType.toUpperCase()){
            case "INGRESO_INVENTARIO", "ENTRADA_DEVOLUCION", "ENTRADA_COMPRA", "AJUSTE_POSITIVO" -> 1;
            case "SALIDA_PRESTAMO", "SALIDA_VENTA", "SALIDA_BAJA", "AJUSTE_NEGATIVO" -> -1;
            default -> 0;
        };
    }


    public List<KardexEntity> getHistoryForUnit(Long unitId) {
        return kardexRepository.findByUnit_UnitIdOrderByDateDesc(unitId);
    }

    public List<KardexEntity> getAllKardex() {
        return kardexRepository.findAll();
    }

    public List<KardexEntity> getHistoryForTool(Long toolId) {
        return kardexRepository.findByUnit_Tool_ToolIdOrderByDateDesc(toolId);
    }

    public List<KardexEntity> getKardexByStore(Long storeId) {
        return kardexRepository.findByStore_StoreIdOrderByDateDesc(storeId);
    }

    public List<KardexEntity> getKardexByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return kardexRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);
    }

    public List<Object[]> getRankingTool(){
        return kardexRepository.findMostRequestedToolsWithLoan();
    }

    // Método con fechas
    public List<Object[]> getRankingTool(LocalDateTime startDate, LocalDateTime endDate){
        return kardexRepository.findMostRequestedToolsWithLoanByDateRange(startDate, endDate);
    }
}