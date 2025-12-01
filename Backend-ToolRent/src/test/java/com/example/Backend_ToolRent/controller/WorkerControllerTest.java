package com.example.Backend_ToolRent.controller;

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
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WorkerController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
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

    private WorkerEntity buildWorker() {
        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(1L);
        worker.setName("Juan Perez");
        worker.setMail("juan@mail.com");
        return worker;
    }

    @Test
    @DisplayName("GET /api/workers/{id} devuelve worker")
    void getWorkerById_returnsOk() throws Exception {
        given(workerService.getWorkerById(1L)).willReturn(buildWorker());

        mockMvc.perform(get("/api/workers/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    @DisplayName("POST /api/workers crea worker")
    void addWorker_returnsOk() throws Exception {
        WorkerEntity created = buildWorker();

        // Mocks necesarios para el flujo del controller
        given(storeRepo.findById(anyLong())).willReturn(Optional.of(new StoreEntity()));
        given(workerService.addWorker(any(WorkerEntity.class), anyString())).willReturn(created);

        Map<String, Object> body = Map.of(
                "name", "Juan Perez",
                "mail", "juan@mail.com",
                "password", "123456",
                "storeId", 1,
                "roleIds", List.of(1)
        );

        mockMvc.perform(post("/api/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mail").value("juan@mail.com"));
    }

    @Test
    @DisplayName("GET /api/workers/by-store/{id} devuelve lista")
    void getWorkersByStore_returnsList() throws Exception {
        given(workerService.getWorkerByStore(10L)).willReturn(List.of(buildWorker()));

        mockMvc.perform(get("/api/workers/by-store/{storeId}", 10L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan Perez"));
    }
}
