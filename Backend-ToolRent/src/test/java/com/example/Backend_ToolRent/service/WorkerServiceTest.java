package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.WorkerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;
import static org.mockito.Mockito.*; // Para verify, times, doThrow
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTest {

    @Mock
    private WorkerRepository workerRepo;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private WorkerService workerService;

    private WorkerEntity buildWorker() {
        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(1L);
        worker.setName("Juan Perez");
        worker.setMail("juan@test.com");

        RolEntity rol = new RolEntity();
        rol.setRolName("ADMIN");
        worker.setRol(Set.of(rol));

        return worker;
    }

    @Test
    @DisplayName("addWorker crea usuario en Keycloak y guarda en BD")
    void addWorker_createsUserAndSaves() {
        WorkerEntity input = buildWorker();

        // Mock de KeycloakService
        given(keycloakService.createUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn("keycloak-id-123");

        given(workerRepo.save(any(WorkerEntity.class))).willAnswer(i -> i.getArgument(0));

        WorkerEntity result = workerService.addWorker(input, "password123");

        assertThat(result.getKeycloakId()).isEqualTo("keycloak-id-123");
        verify(keycloakService).createUser(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(keycloakService).assignRoles(anyString(), anyList());
        verify(workerRepo).save(input);
    }

    @Test
    @DisplayName("getWorkerByName devuelve worker")
    void getWorkerByName_returnsWorker() {
        given(workerRepo.findByNameIgnoreCase("Juan")).willReturn(java.util.Optional.of(buildWorker()));

        WorkerEntity result = workerService.getWorkerByName("Juan");

        assertThat(result.getName()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("updateWorker actualiza datos y roles en Keycloak")
    void updateWorker_success() {
        Long workerId = 1L;
        WorkerEntity existingWorker = buildWorker();
        existingWorker.setKeycloakId("keycloak-123");

        WorkerEntity updateData = new WorkerEntity();
        updateData.setName("New Name");
        updateData.setMail("new@mail.com");
        RolEntity newRol = new RolEntity();
        newRol.setRolName("USER");
        updateData.setRol(Set.of(newRol));

        given(workerRepo.findById(workerId)).willReturn(Optional.of(existingWorker));
        given(workerRepo.save(any(WorkerEntity.class))).willAnswer(i -> i.getArgument(0));

        WorkerEntity result = workerService.updateWorker(workerId, updateData);

        assertThat(result.getName()).isEqualTo("New Name");
        // Verificar sincronización con Keycloak
        verify(keycloakService).updateRoles(eq("keycloak-123"), anyList());
        verify(workerRepo).save(existingWorker);
    }

    @Test
    @DisplayName("updateWorker lanza RuntimeException si falla Keycloak")
    void updateWorker_throwsOnKeycloakError() {
        Long workerId = 1L;
        WorkerEntity existingWorker = buildWorker();
        existingWorker.setKeycloakId("keycloak-123");

        given(workerRepo.findById(workerId)).willReturn(Optional.of(existingWorker));

        // Simular fallo en servicio externo
        doThrow(new RuntimeException("Keycloak error")).when(keycloakService).updateRoles(anyString(), anyList());

        assertThatThrownBy(() -> workerService.updateWorker(workerId, buildWorker()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al actualizar el trabajador");
    }
    @Test
    @DisplayName("deleteWorker elimina de Keycloak y BD")
    void deleteWorker_success() {
        Long workerId = 1L;
        WorkerEntity worker = buildWorker();
        worker.setKeycloakId("key-123");

        given(workerRepo.findById(workerId)).willReturn(Optional.of(worker));

        workerService.deleteWorker(workerId);

        verify(keycloakService).deleteUser("key-123");
        verify(workerRepo).delete(worker);
    }

    @Test
    @DisplayName("deleteWorker lanza excepción si no existe")
    void deleteWorker_notFound() {
        given(workerRepo.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> workerService.deleteWorker(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Worker not found");
    }

    @Test
    @DisplayName("getWorkerByMail lanza excepción si no existe")
    void getWorkerByMail_notFound() {
        given(workerRepo.findByMail(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> workerService.getWorkerByMail("fail@test.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getRolByWorkerId devuelve lista de roles")
    void getRolByWorkerId_success() {
        WorkerEntity worker = buildWorker();
        given(workerRepo.existsById(1L)).willReturn(true);
        given(workerRepo.findById(1L)).willReturn(Optional.of(worker));

        List<RolEntity> roles = workerService.getRolByWorkerId(1L);

        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getRolName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("getWorkerByStore delega en repo")
    void getWorkerByStore_success() {
        workerService.getWorkerByStore(10L);
        verify(workerRepo).findByStore_StoreId(10L);
    }

    @Test
    @DisplayName("deleteWorkerByStore elimina por ID directo")
    void deleteWorkerByStore_success() {
        workerService.deleteWorkerByStore(5L);
        verify(workerRepo).deleteById(5L);
    }


    @Test
    @DisplayName("addWorker maneja error de Keycloak")
    void addWorker_handlesException() {
        WorkerEntity worker = buildWorker();
        given(keycloakService.createUser(any(), any(), any(), any(), any()))
                .willThrow(new RuntimeException("API Error"));

        assertThatThrownBy(() -> workerService.addWorker(worker, "pass"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al crear el trabajador");
    }

}
