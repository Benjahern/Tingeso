package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.repository.RolRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private RolEntity buildRol() {
        RolEntity rol = new RolEntity();
        rol.setRolId(1L);
        rol.setRolName("ADMIN");
        return rol;
    }

    @Test
    @DisplayName("createRol guarda y normaliza nombre a mayúsculas")
    void createRol_savesAndNormalizes() {
        RolEntity input = new RolEntity();
        input.setRolName("admin"); // minúscula

        given(rolRepository.findByRolName("ADMIN")).willReturn(Optional.empty());
        given(rolRepository.save(any(RolEntity.class))).willAnswer(inv -> inv.getArgument(0));

        RolEntity result = rolService.createRol(input);

        assertThat(result.getRolName()).isEqualTo("ADMIN");
        verify(rolRepository).save(input);
    }

    @Test
    @DisplayName("createRol lanza excepción si rol ya existe")
    void createRol_throwsIfExists() {
        RolEntity input = new RolEntity();
        input.setRolName("ADMIN");

        given(rolRepository.findByRolName("ADMIN")).willReturn(Optional.of(new RolEntity()));

        assertThatThrownBy(() -> rolService.createRol(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya existe");
    }

    @Test
    @DisplayName("getRolById devuelve rol si existe")
    void getRolById_returnsRol() {
        given(rolRepository.findById(1L)).willReturn(Optional.of(buildRol()));

        RolEntity result = rolService.getRolById(1L);

        assertThat(result.getRolId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getAllRoles delega en repositorio")
    void getAllRoles_delegatesToRepo() {
        given(rolRepository.findAll()).willReturn(List.of(buildRol()));

        List<RolEntity> result = rolService.getAllRoles();

        assertThat(result).hasSize(1);
    }

    // --- Nuevos Tests para Cobertura del 100% ---

    @Test
    @DisplayName("getRolById lanza EntityNotFoundException si no existe")
    void getRolById_throwsIfNotFound() {
        given(rolRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.getRolById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Rol no encontrado con ID: 999");
    }

    @Test
    @DisplayName("getRolByName devuelve rol si existe")
    void getRolByName_returnsRol() {
        given(rolRepository.findByRolName("ADMIN")).willReturn(Optional.of(buildRol()));

        RolEntity result = rolService.getRolByName("admin"); // minúscula

        assertThat(result.getRolName()).isEqualTo("ADMIN");
        verify(rolRepository).findByRolName("ADMIN"); // Verifica normalización
    }

    @Test
    @DisplayName("getRolByName lanza EntityNotFoundException si no existe")
    void getRolByName_throwsIfNotFound() {
        given(rolRepository.findByRolName("SUPERADMIN")).willReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.getRolByName("SUPERADMIN"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Rol no encontrado con nombre: SUPERADMIN");
    }

    @Test
    @DisplayName("deleteRol elimina rol si existe")
    void deleteRol_deletesRol() {
        given(rolRepository.existsById(1L)).willReturn(true);

        rolService.deleteRol(1L);

        verify(rolRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteRol lanza EntityNotFoundException si no existe")
    void deleteRol_throwsIfNotFound() {
        given(rolRepository.existsById(999L)).willReturn(false);

        assertThatThrownBy(() -> rolService.deleteRol(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se puede eliminar: Rol no encontrado con ID: 999");

        verify(rolRepository, never()).deleteById(anyLong());
    }
}
