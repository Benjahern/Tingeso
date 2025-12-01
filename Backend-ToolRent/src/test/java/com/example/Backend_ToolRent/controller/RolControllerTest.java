package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.service.RolService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RolController.class)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RolService rolService;

    @MockitoBean
    private JwtDecoder jwtDecoder; // Necesario para iniciar el contexto de seguridad

    private RolEntity buildRol() {
        RolEntity rol = new RolEntity();
        rol.setRolId(1L);
        rol.setRolName("ADMIN");
        return rol;
    }

    @Test
    @DisplayName("GET /api/roles devuelve lista")
    void getAllRoles_returnsOk() throws Exception {
        given(rolService.getAllRoles()).willReturn(List.of(buildRol()));

        mockMvc.perform(get("/api/roles")
                        .with(jwt()) // Simula autenticación
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rolName").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /api/roles/{id} devuelve rol")
    void getRolById_returnsOk() throws Exception {
        given(rolService.getRolById(anyLong())).willReturn(buildRol());

        mockMvc.perform(get("/api/roles/{id}", 1L)
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rolName").value("ADMIN"));
    }

    // --- TEST NUEVO QUE FALTABA ---
    @Test
    @DisplayName("GET /api/roles/by-name devuelve rol por nombre")
    void getRolByName_returnsOk() throws Exception {
        given(rolService.getRolByName("ADMIN")).willReturn(buildRol());

        mockMvc.perform(get("/api/roles/by-name")
                        .param("name", "ADMIN")
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rolName").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/roles crea rol")
    void createRol_returnsCreated() throws Exception {
        RolEntity input = buildRol();
        given(rolService.createRol(any(RolEntity.class))).willReturn(input);

        mockMvc.perform(post("/api/roles")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rolName").value("ADMIN"));
    }

    @Test
    @DisplayName("DELETE /api/roles/{id} elimina rol")
    void deleteRol_returnsNoContent() throws Exception {
        doNothing().when(rolService).deleteRol(1L);

        mockMvc.perform(delete("/api/roles/{id}", 1L)
                        .with(jwt()))
                .andExpect(status().isNoContent());
    }
}
