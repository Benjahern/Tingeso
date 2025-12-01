package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.*;
import com.example.Backend_ToolRent.repository.KardexRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @DisplayName("registerMovement guarda movimiento correctamente")
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
}
