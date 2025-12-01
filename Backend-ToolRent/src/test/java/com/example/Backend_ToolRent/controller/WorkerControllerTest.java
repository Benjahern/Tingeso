package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.entity.StoreEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.RolRepository;
import com.example.Backend_ToolRent.repository.StoreRepository;
import com.example.Backend_ToolRent.service.RolService;
import com.example.Backend_ToolRent.service.WorkerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Usamos configuración estándar con seguridad mockeada para soportar @PreAuthorize y jwt()
@WebMvcTest(controllers = WorkerController.class)
class WorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkerService workerService;

    @MockitoBean
    private RolService rolService;

    @MockitoBean
    private RolRepository rolRepo;

    @MockitoBean
    private StoreRepository storeRepo;

    @MockitoBean
    private JwtDecoder jwtDecoder; // Necesario para arrancar el contexto de seguridad

    private WorkerEntity buildWorker() {
        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(1L);
        worker.setName("Juan Perez");
        worker.setMail("juan@mail.com");
        return worker;
    }

    // --- GET Tests ---

    @Test
    @DisplayName("GET /api/workers/me devuelve el usuario autenticado")
    void getWorkerMe_returnsOk() throws Exception {
        given(workerService.getWorkerByName("test-user")).willReturn(buildWorker());

        mockMvc.perform(get("/api/workers/me")
                        .with(jwt().jwt(jwt -> jwt.subject("test-user"))) // Mock del token JWT
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/workers devuelve todos los workers")
    void getAllWorkers_returnsList() throws Exception {
        given(workerService.getAllWorkers()).willReturn(List.of(buildWorker()));

        mockMvc.perform(get("/api/workers")
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/workers/{id} devuelve worker por ID")
    void getWorkerById_returnsOk() throws Exception {
        given(workerService.getWorkerById(1L)).willReturn(buildWorker());

        mockMvc.perform(get("/api/workers/{id}", 1L)
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/workers/by-store/{id} devuelve lista")
    void getWorkersByStore_returnsList() throws Exception {
        given(workerService.getWorkerByStore(10L)).willReturn(List.of(buildWorker()));

        mockMvc.perform(get("/api/workers/by-store/{storeId}", 10L)
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/workers/{id}/roles devuelve roles")
    void getRolesByWorker_returnsList() throws Exception {
        RolEntity rol = new RolEntity();
        rol.setRolName("ADMIN");
        given(workerService.getRolByWorkerId(1L)).willReturn(List.of(rol));

        mockMvc.perform(get("/api/workers/{id}/roles", 1L)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rolName").value("ADMIN"));
    }

    // --- POST Tests (Add Worker) ---

    @Test
    @DisplayName("POST /api/workers crea worker con roles y tienda")
    void addWorker_withRoles_returnsOk() throws Exception {
        WorkerEntity created = buildWorker();
        StoreEntity store = new StoreEntity();
        store.setStoreId(1L);

        given(storeRepo.findById(1L)).willReturn(Optional.of(store));
        given(rolRepo.findAllById(anyList())).willReturn(List.of(new RolEntity()));
        given(workerService.addWorker(any(WorkerEntity.class), anyString())).willReturn(created);

        Map<String, Object> body = Map.of(
                "name", "Juan Perez",
                "mail", "juan@mail.com",
                "password", "123456",
                "storeId", 1,
                "roleIds", List.of(1)
        );

        mockMvc.perform(post("/api/workers")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mail").value("juan@mail.com"));
    }

    @Test
    @DisplayName("POST /api/workers crea worker SIN roles (Branch else)")
    void addWorker_noRoles_returnsOk() throws Exception {
        WorkerEntity created = buildWorker();
        StoreEntity store = new StoreEntity();

        given(storeRepo.findById(1L)).willReturn(Optional.of(store));
        given(workerService.addWorker(any(WorkerEntity.class), anyString())).willReturn(created);

        // Enviamos roleIds como null o lista vacía para entrar en el 'else'
        Map<String, Object> body = Map.of(
                "name", "Juan Perez",
                "mail", "juan@mail.com",
                "password", "123456",
                "storeId", 1
                // "roleIds" omitido es null
        );

        mockMvc.perform(post("/api/workers")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    // --- PUT Tests (Update Worker) ---

    @Test
    @DisplayName("PUT /api/workers/{id} actualiza worker con roles")
    void updateWorker_withRoles_returnsOk() throws Exception {
        WorkerEntity updated = buildWorker();

        given(rolRepo.findAllById(anyList())).willReturn(List.of(new RolEntity()));
        given(workerService.updateWorker(eq(1L), any(WorkerEntity.class))).willReturn(updated);

        Map<String, Object> body = Map.of(
                "name", "Juan Modificado",
                "mail", "juan@mail.com",
                "roleIds", List.of(1)
        );

        mockMvc.perform(put("/api/workers/{id}", 1L)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    @DisplayName("PUT /api/workers/{id} actualiza worker SIN roles (Branch else)")
    void updateWorker_noRoles_returnsOk() throws Exception {
        WorkerEntity updated = buildWorker();
        given(workerService.updateWorker(eq(1L), any(WorkerEntity.class))).willReturn(updated);

        Map<String, Object> body = Map.of(
                "name", "Juan Modificado",
                "mail", "juan@mail.com"
        );

        mockMvc.perform(put("/api/workers/{id}", 1L)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    // --- DELETE Tests ---

    @Test
    @DisplayName("DELETE /api/workers/{id} elimina worker")
    void deleteWorker_returnsNoContent() throws Exception {
        doNothing().when(workerService).deleteWorker(1L);

        mockMvc.perform(delete("/api/workers/{id}", 1L)
                        .with(jwt()))
                .andExpect(status().isNoContent());
    }
}
