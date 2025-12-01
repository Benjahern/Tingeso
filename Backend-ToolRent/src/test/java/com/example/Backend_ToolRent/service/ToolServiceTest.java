package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.repository.ToolRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolServiceTest {

    @Mock
    private ToolRepository toolRepository;

    @InjectMocks
    private ToolService toolService;

    private ToolEntity buildTool() {
        ToolEntity tool = new ToolEntity();
        tool.setToolId(1L);
        tool.setToolName("Taladro");
        tool.setDescription("Taladro eléctrico");
        tool.setCategory("Herramienta eléctrica");
        tool.setReplacementValue(100.0);
        tool.setStock(10);
        tool.setDailyPrice(5.0);
        return tool;
    }

    @Test
    @DisplayName("saveTool debe guardar y retornar la herramienta")
    void saveTool_shouldReturnSavedTool() {
        ToolEntity tool = buildTool();
        given(toolRepository.save(any(ToolEntity.class))).willReturn(tool);

        ToolEntity result = toolService.saveTool(tool);

        assertThat(result).isNotNull();
        assertThat(result.getToolName()).isEqualTo("Taladro");
        verify(toolRepository, times(1)).save(tool);
    }

    @Test
    @DisplayName("getToolById debe retornar herramienta cuando existe")
    void getToolById_whenExists_returnsTool() {
        ToolEntity tool = buildTool();
        given(toolRepository.findById(1L)).willReturn(Optional.of(tool));

        ToolEntity result = toolService.getToolById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getToolId()).isEqualTo(1L);
        verify(toolRepository).findById(1L);
    }

    @Test
    @DisplayName("getToolById debe lanzar EntityNotFoundException cuando no existe")
    void getToolById_whenNotExists_throwsException() {
        given(toolRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> toolService.getToolById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Tool not found");

        verify(toolRepository).findById(1L);
    }

    @Test
    @DisplayName("getAllTool debe retornar lista de herramientas")
    void getAllTool_returnsList() {
        ToolEntity tool = buildTool();
        given(toolRepository.findAll()).willReturn(List.of(tool));

        List<ToolEntity> result = toolService.getAllTool();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToolName()).isEqualTo("Taladro");
        verify(toolRepository).findAll();
    }

    @Test
    @DisplayName("deleteToolById debe llamar a deleteById del repositorio")
    void deleteToolById_callsRepository() {
        Long id = 1L;

        toolService.deleteToolById(id);

        verify(toolRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("getToolByName debe retornar herramienta cuando existe")
    void getToolByName_whenExists_returnsTool() {
        ToolEntity tool = buildTool();
        given(toolRepository.findByToolNameContainingIgnoreCase("taladro"))
                .willReturn(Optional.of(tool));

        ToolEntity result = toolService.getToolByName("taladro");

        assertThat(result).isNotNull();
        assertThat(result.getToolName()).isEqualTo("Taladro");
        verify(toolRepository).findByToolNameContainingIgnoreCase("taladro");
    }

    @Test
    @DisplayName("setDailyPrice debe actualizar el precio diario")
    void setDailyPrice_updatesDailyPrice() {
        ToolEntity tool = buildTool();
        given(toolRepository.findById(1L)).willReturn(Optional.of(tool));
        given(toolRepository.save(any(ToolEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        ToolEntity result = toolService.setDailyPrice(1L, 20.0);

        assertThat(result.getDailyPrice()).isEqualTo(20.0);
        verify(toolRepository).findById(1L);
        verify(toolRepository).save(any(ToolEntity.class));
    }

    @Test
    @DisplayName("setReplacementValue debe actualizar el valor de reposición")
    void setReplacementValue_updatesReplacementValue() {
        ToolEntity tool = buildTool();
        given(toolRepository.findById(1L)).willReturn(Optional.of(tool));
        given(toolRepository.save(any(ToolEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        ToolEntity result = toolService.setReplacementValue(1L, 500.0);

        assertThat(result.getReplacementValue()).isEqualTo(500.0);
        verify(toolRepository).findById(1L);
        verify(toolRepository).save(any(ToolEntity.class));
    }

    @Test
    @DisplayName("updateTool debe actualizar nombre, descripción y categoría")
    void updateTool_updatesFields() {
        ToolEntity existing = buildTool();
        given(toolRepository.findById(1L)).willReturn(Optional.of(existing));

        ToolEntity changes = new ToolEntity();
        changes.setToolName("Taladro Pro");
        changes.setDescription("Taladro profesional");
        changes.setCategory("Profesional");

        given(toolRepository.save(any(ToolEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        ToolEntity result = toolService.updateTool(1L, changes);

        assertThat(result.getToolName()).isEqualTo("Taladro Pro");
        assertThat(result.getDescription()).isEqualTo("Taladro profesional");
        assertThat(result.getCategory()).isEqualTo("Profesional");
        verify(toolRepository).findById(1L);
        verify(toolRepository).save(any(ToolEntity.class));
    }
}
