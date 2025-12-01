package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.KardexEntity;
import com.example.Backend_ToolRent.service.KardexService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = KardexController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class KardexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KardexService kardexService;

    @Test
    @DisplayName("GET /api/kardex/getAll devuelve lista")
    void getAllKardex_returnsOk() throws Exception {
        KardexEntity k = new KardexEntity();
        k.setKardexId(100L);

        given(kardexService.getAllKardex()).willReturn(List.of(k));

        mockMvc.perform(get("/api/kardex/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kardexId").value(100));
    }

    @Test
    @DisplayName("GET /api/kardex/history/unit/{id} devuelve historial")
    void getHistoryForUnit_returnsOk() throws Exception {
        given(kardexService.getHistoryForUnit(anyLong())).willReturn(List.of(new KardexEntity()));

        mockMvc.perform(get("/api/kardex/history/unit/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/kardex/ranking funciona sin fechas (usa defaults)")
    void getRanking_returnsOk() throws Exception {
        // Mockeamos la respuesta del ranking (Lista vacía o de objetos)
        given(kardexService.getRankingTool(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of());

        mockMvc.perform(get("/api/kardex/ranking")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
