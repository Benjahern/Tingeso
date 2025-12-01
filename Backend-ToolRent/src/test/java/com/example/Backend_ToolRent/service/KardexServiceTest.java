package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.*;
import com.example.Backend_ToolRent.repository.KardexRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KardexServiceTest {

    @Mock
    private KardexRepository kardexRepository;

    @InjectMocks
    private KardexService kardexService;

    private UnitEntity buildUnit() {
        UnitEntity unit = new UnitEntity();
        ToolEntity tool = new ToolEntity();
        tool.setStock(10);
        tool.setReplacementValue(1000.0);
        unit.setTool(tool);
        return unit;
    }

    @Test
    @DisplayName("registerMovement guarda movimiento correctamente (Tipo 1)")
    void registerMovement_savesEntity() {
        UnitEntity unit = buildUnit();
        WorkerEntity worker = new WorkerEntity();
        StoreEntity store = new StoreEntity();
        worker.setStore(store);

        given(kardexRepository.save(any(KardexEntity.class))).willAnswer(i -> i.getArgument(0));

        // Ejecutar movimiento "INGRESO_INVENTARIO" (Type = 1)
        kardexService.registerMovement(unit, "INGRESO_INVENTARIO", worker, null, "Test comment");

        verify(kardexRepository).save(any(KardexEntity.class));
    }

    @Test
    @DisplayName("getHistoryForUnit retorna lista")
    void getHistoryForUnit_returnsList() {
        given(kardexRepository.findByUnit_UnitIdOrderByDateDesc(1L)).willReturn(List.of(new KardexEntity()));
        List<KardexEntity> result = kardexService.getHistoryForUnit(1L);
        assertThat(result).hasSize(1);
    }

    // --- Nuevos Tests para Cobertura del 100% ---

    @Test
    @DisplayName("registerMovement calcula tipo -1 para SALIDA_PRESTAMO")
    void registerMovement_negativeType() {
        UnitEntity unit = buildUnit();
        WorkerEntity worker = new WorkerEntity();
        worker.setStore(new StoreEntity());

        given(kardexRepository.save(any(KardexEntity.class))).willAnswer(i -> {
            KardexEntity k = i.getArgument(0);
            assertThat(k.getType()).isEqualTo(-1);
            return k;
        });

        kardexService.registerMovement(unit, "SALIDA_PRESTAMO", worker, null, "Salida");
    }

    @Test
    @DisplayName("registerMovement calcula tipo 0 para movimiento desconocido")
    void registerMovement_unknownType() {
        UnitEntity unit = buildUnit();
        WorkerEntity worker = new WorkerEntity();
        worker.setStore(new StoreEntity());

        given(kardexRepository.save(any(KardexEntity.class))).willAnswer(i -> {
            KardexEntity k = i.getArgument(0);
            assertThat(k.getType()).isEqualTo(0);
            return k;
        });

        kardexService.registerMovement(unit, "MOVIMIENTO_EXTRAÑO", worker, null, "Test");
    }

    @Test
    @DisplayName("registerMovement maneja replacementValue null (UnitCost ZERO)")
    void registerMovement_nullReplacementValue() {
        UnitEntity unit = buildUnit();
        unit.getTool().setReplacementValue(null); // Forzamos null
        WorkerEntity worker = new WorkerEntity();
        worker.setStore(new StoreEntity());

        given(kardexRepository.save(any(KardexEntity.class))).willAnswer(i -> {
            KardexEntity k = i.getArgument(0);
            assertThat(k.getUnitCost()).isEqualTo(BigDecimal.ZERO);
            return k;
        });

        kardexService.registerMovement(unit, "INGRESO_INVENTARIO", worker, null, "Null Value");
    }

    @Test
    @DisplayName("getAllKardex retorna lista")
    void getAllKardex_returnsList() {
        given(kardexRepository.findAll()).willReturn(List.of(new KardexEntity()));
        List<KardexEntity> result = kardexService.getAllKardex();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getHistoryForTool retorna lista")
    void getHistoryForTool_returnsList() {
        given(kardexRepository.findByUnit_Tool_ToolIdOrderByDateDesc(10L)).willReturn(List.of(new KardexEntity()));
        List<KardexEntity> result = kardexService.getHistoryForTool(10L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getKardexByStore retorna lista")
    void getKardexByStore_returnsList() {
        given(kardexRepository.findByStore_StoreIdOrderByDateDesc(5L)).willReturn(List.of(new KardexEntity()));
        List<KardexEntity> result = kardexService.getKardexByStore(5L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getKardexByDateRange retorna lista")
    void getKardexByDateRange_returnsList() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        given(kardexRepository.findByDateBetweenOrderByDateDesc(start, end)).willReturn(List.of(new KardexEntity()));

        List<KardexEntity> result = kardexService.getKardexByDateRange(start, end);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getRankingTool (sin args) retorna lista de objetos/arrays")
    void getRankingTool_noArgs_returnsList() {
        // getRankingTool devuelve List<Object[]> normalmente, simulamos una lista vacía o con objetos
        given(kardexRepository.findMostRequestedToolsWithLoan()).willReturn(List.of());
        List<Object[]> result = kardexService.getRankingTool();
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getRankingTool (con fechas) retorna lista")
    void getRankingTool_withDates_returnsList() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        given(kardexRepository.findMostRequestedToolsWithLoanByDateRange(start, end)).willReturn(List.of());

        List<Object[]> result = kardexService.getRankingTool(start, end);
        assertThat(result).isNotNull();
    }
}
