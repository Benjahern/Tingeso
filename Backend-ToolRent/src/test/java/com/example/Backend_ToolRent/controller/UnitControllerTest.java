package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.entity.UnitEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.ClientRepository;
import com.example.Backend_ToolRent.repository.ToolRepository;
import com.example.Backend_ToolRent.repository.UnitRepository;
import com.example.Backend_ToolRent.service.KardexService;
import com.example.Backend_ToolRent.service.UnitService;
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

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UnitController.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks de Servicios ---
    @MockitoBean
    private UnitService unitService;

    @MockitoBean
    private WorkerService workerService;

    @MockitoBean
    private KardexService kardexService;

    // --- Mocks de Repositorios (Usados directamente en el Controller) ---
    @MockitoBean
    private UnitRepository unitRepo;

    @MockitoBean
    private ClientRepository clientRepo;

    @MockitoBean
    private ToolRepository toolRepo;

    // --- Mock de Seguridad (CRÍTICO PARA QUE ARRANQUE) ---
    @MockitoBean
    private JwtDecoder jwtDecoder;

    // --- Helpers ---
    private UnitEntity buildUnit() {
        UnitEntity unit = new UnitEntity();
        unit.setUnitId(1L);
        unit.setStatus("Disponible");
        unit.setCondition("Good");
        return unit;
    }

    private WorkerEntity buildWorker() {
        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(5L);
        worker.setMail("test@empleado.com");
        return worker;
    }

    // --- Tests GET Básicos ---

    @Test
    @DisplayName("GET /api/units - Debe retornar lista de unidades")
    void getAllUnits() throws Exception {
        when(unitService.findAllUnit()).thenReturn(List.of(buildUnit()));

        mockMvc.perform(get("/api/units")
                        .with(jwt())) // Autenticado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unitId").value(1))
                .andExpect(jsonPath("$[0].status").value("Disponible"));
    }

    @Test
    @DisplayName("GET /api/units/{id} - Debe retornar unidad por ID")
    void getUnitById() throws Exception {
        when(unitService.findUnitById(1L)).thenReturn(buildUnit());

        mockMvc.perform(get("/api/units/{id}", 1L)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unitId").value(1));
    }

    @Test
    @DisplayName("GET /api/units/by-tool/{toolId} - Debe retornar unidades por herramienta")
    void getUnitsByToolId() throws Exception {
        when(unitService.getUnitByToolID(10L)).thenReturn(List.of(buildUnit()));

        mockMvc.perform(get("/api/units/by-tool/{toolId}", 10L)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unitId").value(1));
    }

    // --- Tests de Búsqueda (Search) ---

    @Test
    @DisplayName("GET /api/units/search?status=... - Busca por status")
    void searchUnits_byStatus() throws Exception {
        when(unitService.getUnitByStatus("Disponible")).thenReturn(List.of(buildUnit()));

        mockMvc.perform(get("/api/units/search")
                        .param("status", "Disponible")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Disponible"));
    }

    @Test
    @DisplayName("GET /api/units/search?condition=... - Busca por condición")
    void searchUnits_byCondition() throws Exception {
        when(unitService.getUnitByCondition("Good")).thenReturn(List.of(buildUnit()));

        mockMvc.perform(get("/api/units/search")
                        .param("condition", "Good")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].condition").value("Good"));
    }

    @Test
    @DisplayName("GET /api/units/search?toolName=... - Busca por nombre de herramienta")
    void searchUnits_byToolName() throws Exception {
        when(unitService.getUnitByName("Martillo")).thenReturn(List.of(buildUnit()));

        mockMvc.perform(get("/api/units/search")
                        .param("toolName", "Martillo")
                        .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/units/search sin params - Retorna Bad Request")
    void searchUnits_noParams() throws Exception {
        mockMvc.perform(get("/api/units/search")
                        .with(jwt()))
                .andExpect(status().isBadRequest());
    }

    // --- Tests de Creación y Edición ---

    @Test
    @DisplayName("POST /api/units - Crea unidad exitosamente")
    void createUnit() throws Exception {
        String userEmail = "test@empleado.com";
        WorkerEntity worker = buildWorker();
        UnitEntity inputUnit = buildUnit();
        UnitEntity createdUnit = buildUnit();
        createdUnit.setUnitId(99L);

        when(workerService.getWorkerByMail(userEmail)).thenReturn(worker);
        when(unitService.createUnit(any(UnitEntity.class), eq(5L))).thenReturn(createdUnit);

        mockMvc.perform(post("/api/units")
                        .with(jwt().jwt(jwt -> jwt.claim("email", userEmail)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUnit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unitId").value(99));
    }

    @Test
    @DisplayName("PUT /api/units/{id} - Actualiza unidad")
    void updateUnit() throws Exception {
        String userEmail = "test@empleado.com";
        WorkerEntity worker = buildWorker();
        UnitEntity updateInfo = buildUnit();
        updateInfo.setStatus("Mantenimiento");

        when(workerService.getWorkerByMail(userEmail)).thenReturn(worker);
        when(unitService.updateUnit(eq(1L), any(UnitEntity.class), eq(5L))).thenReturn(updateInfo);

        mockMvc.perform(put("/api/units/{id}", 1L)
                        .with(jwt().jwt(jwt -> jwt.claim("email", userEmail)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Mantenimiento"));
    }

    // --- Tests Específicos (Decommission y Patch) ---

    @Test
    @DisplayName("POST /decommission - Da de baja unidad y registra movimiento")
    void decommissionUnit() throws Exception {
        Long unitId = 10L;
        String userEmail = "admin@toolrent.com";
        WorkerEntity worker = new WorkerEntity(); worker.setUserId(2L);

        ToolEntity tool = new ToolEntity();
        tool.setStock(10);

        UnitEntity unit = new UnitEntity();
        unit.setUnitId(unitId);
        unit.setTool(tool);

        // Mocks para la lógica interna del controller
        when(unitService.findUnitById(unitId)).thenReturn(unit);
        when(workerService.getWorkerByMail(userEmail)).thenReturn(worker);

        Map<String, String> requestBody = Map.of(
                "condition", "Irreparable",
                "comment", "Caída desde altura"
        );

        mockMvc.perform(post("/api/units/{unitId}/decommission", unitId)
                        .with(jwt().jwt(jwt -> jwt.claim("email", userEmail)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Unit decommissioned successfully"));

        // Verificamos que se haya guardado el cambio de estado y descontado stock
        verify(unitRepo).save(argThat(u -> "Dado de Baja".equals(u.getStatus())));
        verify(toolRepo).save(argThat(t -> t.getStock() == 9)); // 10 - 1 = 9
    }

    @Test
    @DisplayName("PATCH /api/units/{id}/status - Actualiza estado parcialmente")
    void updateUnitStatus() throws Exception {
        Long unitId = 1L;
        String userEmail = "test@empleado.com";
        WorkerEntity worker = buildWorker();
        UnitEntity existingUnit = buildUnit();

        when(workerService.getWorkerByMail(userEmail)).thenReturn(worker);
        when(unitService.findUnitById(unitId)).thenReturn(existingUnit);
        when(unitService.updateUnit(eq(unitId), any(UnitEntity.class), eq(5L))).thenReturn(existingUnit);

        Map<String, String> patchBody = Map.of(
                "status", "En Reparación",
                "condition", "Dañado"
        );

        mockMvc.perform(patch("/api/units/{id}/status", unitId)
                        .with(jwt().jwt(jwt -> jwt.claim("email", userEmail)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchBody)))
                .andExpect(status().isOk());

        // Verificamos que el objeto pasado al servicio tenga los nuevos valores
        verify(unitService).updateUnit(eq(unitId), argThat(u ->
                "En Reparación".equals(u.getStatus()) && "Dañado".equals(u.getCondition())
        ), eq(5L));
    }
}
