package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.entity.UnitEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.ToolRepository;
import com.example.Backend_ToolRent.repository.UnitRepository;
import com.example.Backend_ToolRent.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    // 1. DEFINICIÓN DE MOCKS (Nombres consistentes con el constructor del servicio)
    @Mock
    private UnitRepository unitRepo;

    @Mock
    private ToolRepository toolRepo;

    @Mock
    private WorkerRepository workerRepo;

    @Mock
    private KardexService kardexService;

    // 2. INYECCIÓN AUTOMÁTICA
    @InjectMocks
    private UnitService unitService;

    // --- Helper para crear unidades rápidas ---
    private UnitEntity buildUnit() {
        UnitEntity unit = new UnitEntity();
        unit.setUnitId(1L);
        unit.setCondition("Bueno");
        unit.setStatus("Disponible");
        // Evitamos NullPointer si el servicio accede al tool
        ToolEntity tool = new ToolEntity();
        tool.setToolId(10L);
        unit.setTool(tool);
        return unit;
    }

    // --- TESTS ---

    @Test
    @DisplayName("findUnitById devuelve unidad si existe")
    void findUnitById_success() {
        // Preparar
        Long unitId = 1L;
        UnitEntity unit = buildUnit();

        // Configurar Mock: Usamos anyLong() para asegurar que el match funcione
        given(unitRepo.findById(anyLong())).willReturn(Optional.of(unit));

        // Ejecutar
        UnitEntity result = unitService.findUnitById(unitId);

        // Verificar
        assertThat(result).isNotNull();
        assertThat(result.getUnitId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findUnitById lanza excepción si no existe")
    void findUnitById_notFound() {
        // Configurar Mock para devolver vacío
        given(unitRepo.findById(anyLong())).willReturn(Optional.empty());

        // Verificar excepción
        assertThatThrownBy(() -> unitService.findUnitById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Unit Not Found");
    }

    @Test
    @DisplayName("createUnit crea unidad y registra kardex correctamente")
    void createUnit_success() {
        Long workerId = 1L;

        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(workerId);

        ToolEntity tool = new ToolEntity();
        tool.setToolId(10L);

        UnitEntity unitInput = new UnitEntity();
        unitInput.setTool(tool);
        unitInput.setStatus("Disponible");

        // Simulamos la unidad guardada (con ID asignado)
        UnitEntity savedUnit = buildUnit();
        savedUnit.setUnitId(100L);

        given(workerRepo.findById(workerId)).willReturn(Optional.of(worker));
        given(toolRepo.findById(10L)).willReturn(Optional.of(tool));
        given(unitRepo.save(any(UnitEntity.class))).willReturn(savedUnit);

        UnitEntity result = unitService.createUnit(unitInput, workerId);

        assertThat(result).isNotNull();
        assertThat(result.getUnitId()).isEqualTo(100L);
        // Verificar interacción con Kardex
        verify(kardexService).registerMovement(any(), eq("INGRESO_INVENTARIO"), eq(worker), isNull(), anyString());
    }

    @Test
    @DisplayName("updateUnit actualiza y registra movimiento si hay cambios")
    void updateUnit_successWithChanges() {
        Long unitId = 1L;
        Long workerId = 1L;

        WorkerEntity worker = new WorkerEntity();

        UnitEntity existingUnit = buildUnit(); // Estado: Disponible, Condición: Bueno

        UnitEntity updateInfo = new UnitEntity();
        updateInfo.setStatus("En reparación");
        updateInfo.setCondition("Dañado");

        // Mock de findById necesario para updateUnit
        given(workerRepo.findById(workerId)).willReturn(Optional.of(worker));
        given(unitRepo.findById(unitId)).willReturn(Optional.of(existingUnit));

        // Mock del save (devolvemos la misma instancia modificada)
        given(unitRepo.save(any(UnitEntity.class))).willAnswer(i -> i.getArgument(0));

        UnitEntity result = unitService.updateUnit(unitId, updateInfo, workerId);

        assertThat(result.getStatus()).isEqualTo("En reparación");
        assertThat(result.getCondition()).isEqualTo("Dañado");

        verify(kardexService).registerMovement(any(), eq("AJUSTE_ESTADO"), eq(worker), isNull(), anyString());
    }

    @Test
    @DisplayName("getAllUnits delega en repo")
    void getAllUnits_delegatesToRepository() {
        given(unitRepo.findAll()).willReturn(List.of(buildUnit()));
        List<UnitEntity> result = unitService.findAllUnit(); // Ojo: el método en service es findAllUnit
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getUnitsByStatus delega en repo")
    void getUnitsByStatus_delegatesToRepository() {
        given(unitRepo.findByStatus("Disponible")).willReturn(List.of(buildUnit()));
        List<UnitEntity> result = unitService.getUnitByStatus("Disponible");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getUnitByName delega en repo")
    void getUnitByName_delegatesToRepository() {
        given(unitRepo.findByTool_ToolNameContainingIgnoreCase("Taladro")).willReturn(List.of(buildUnit()));
        unitService.getUnitByName("Taladro");
        verify(unitRepo).findByTool_ToolNameContainingIgnoreCase("Taladro");
    }

    @Test
    @DisplayName("getUnitByToolID delega en repo")
    void getUnitByToolID_delegatesToRepository() {
        given(unitRepo.findByTool_ToolId(10L)).willReturn(List.of(buildUnit()));
        unitService.getUnitByToolID(10L);
        verify(unitRepo).findByTool_ToolId(10L);
    }
}
