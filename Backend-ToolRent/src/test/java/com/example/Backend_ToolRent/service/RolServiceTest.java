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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
}
