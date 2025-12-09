package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.StoreEntity;
import com.example.Backend_ToolRent.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException; // Importante para el mock de excepciones
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StoreController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StoreService storeService;

    private StoreEntity buildStore() {
        StoreEntity store = new StoreEntity();
        store.setStoreId(1L);
        store.setStoreName("Tienda Central");
        store.setDailyFine(1000);
        return store;
    }

    @Test
    @DisplayName("GET /api/stores/{id} devuelve store")
    void getStoreById_returnsOk() throws Exception {
        given(storeService.getStoreById(1L)).willReturn(buildStore());

        mockMvc.perform(get("/api/stores/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("Tienda Central"));
    }

    @Test
    @DisplayName("POST /api/stores crea store")
    void createStore_returnsCreated() throws Exception {
        StoreEntity input = buildStore();
        given(storeService.saveStore(any(StoreEntity.class))).willReturn(input);

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.storeId").value(1));
    }

    @Test
    @DisplayName("PUT /api/stores/{id}/daily-fine actualiza multa")
    void updateDailyFine_returnsOk() throws Exception {
        StoreEntity updated = buildStore();
        updated.setDailyFine(2000);
        given(storeService.updateDailyFine(anyLong(), anyInt())).willReturn(updated);

        Map<String, Integer> payload = Map.of("newFine", 2000);

        mockMvc.perform(put("/api/stores/{id}/daily-fine", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyFine").value(2000));
    }

    // --- Nuevos Tests para Cobertura de Errores ---

    @Test
    @DisplayName("GET /api/stores/{id} devuelve 404 si no existe")
    void getStoreById_notFound_returns404() throws Exception {
        given(storeService.getStoreById(anyLong()))
                .willThrow(new EntityNotFoundException("Store not found"));

        mockMvc.perform(get("/api/stores/{id}", 999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/stores/{id}/daily-fine devuelve 404 si no existe")
    void updateDailyFine_notFound_returns404() throws Exception {
        given(storeService.updateDailyFine(anyLong(), anyInt()))
                .willThrow(new EntityNotFoundException("Store not found"));

        Map<String, Integer> payload = Map.of("newFine", 2000);

        mockMvc.perform(put("/api/stores/{id}/daily-fine", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }
}
