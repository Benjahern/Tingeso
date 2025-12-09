package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.service.FileStorageService;
import com.example.Backend_ToolRent.service.ToolService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ToolController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ToolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ToolService toolService;

    @MockitoBean
    private FileStorageService fileStorageService;

    private ToolEntity buildTool() {
        ToolEntity tool = new ToolEntity();
        tool.setToolId(1L);
        tool.setToolName("Taladro");
        tool.setDescription("Taladro eléctrico");
        tool.setCategory("Herramienta eléctrica");
        tool.setReplacementValue(100.0);
        tool.setStock(10);
        tool.setDailyPrice(5.0);
        tool.setImagePath("/img/taladro.png");
        return tool;
    }

    @Test
    @DisplayName("GET /api/tools debe retornar lista de herramientas")
    void getAllTools_returnsOkWithList() throws Exception {
        given(toolService.getAllTool()).willReturn(List.of(buildTool()));

        mockMvc.perform(get("/api/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toolName").value("Taladro"));
    }

    @Test
    @DisplayName("GET /api/tools/{id} debe retornar herramienta por id")
    void getToolById_returnsOk() throws Exception {
        given(toolService.getToolById(1L)).willReturn(buildTool());

        mockMvc.perform(get("/api/tools/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolId").value(1L))
                .andExpect(jsonPath("$.toolName").value("Taladro"));
    }

    @Test
    @DisplayName("GET /api/tools/search?name= debe buscar por nombre")
    void searchToolByName_returnsOk() throws Exception {
        given(toolService.getToolByName("taladro")).willReturn(buildTool());

        mockMvc.perform(get("/api/tools/search")
                        .param("name", "taladro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolName").value("Taladro"));
    }

    @Test
    @DisplayName("POST /api/tools debe crear herramienta")
    void createTool_returnsCreated() throws Exception {
        ToolEntity saved = buildTool();
        given(toolService.saveTool(Mockito.any(ToolEntity.class))).willReturn(saved);

        String body = """
                {
                  "toolName": "Taladro",
                  "description": "Taladro eléctrico",
                  "category": "Herramienta eléctrica",
                  "replacementValue": 100.0,
                  "stock": 10,
                  "dailyPrice": 5.0
                }
                """;

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.toolName").value("Taladro"));
    }

    @Test
    @DisplayName("PUT /api/tools/{id} debe actualizar herramienta")
    void updateTool_returnsOk() throws Exception {
        ToolEntity updated = buildTool();
        updated.setToolName("Taladro Pro");
        given(toolService.updateTool(eq(1L), Mockito.any(ToolEntity.class))).willReturn(updated);

        String body = """
                {
                  "toolName": "Taladro Pro",
                  "description": "Taladro eléctrico",
                  "category": "Herramienta eléctrica",
                  "replacementValue": 100.0,
                  "stock": 10,
                  "dailyPrice": 5.0
                }
                """;

        mockMvc.perform(put("/api/tools/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolName").value("Taladro Pro"));
    }

    @Test
    @DisplayName("PUT /api/tools/{id}/daily-price debe actualizar precio diario")
    void setDailyPrice_returnsOk() throws Exception {
        ToolEntity updated = buildTool();
        updated.setDailyPrice(20.0);
        given(toolService.setDailyPrice(1L, 20.0)).willReturn(updated);

        mockMvc.perform(put("/api/tools/{id}/daily-price", 1L)
                        .param("price", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyPrice").value(20.0));
    }

    @Test
    @DisplayName("PUT /api/tools/{id}/replacement-value debe actualizar valor de reposición")
    void setReplacementValue_returnsOk() throws Exception {
        ToolEntity updated = buildTool();
        updated.setReplacementValue(500.0);
        given(toolService.setReplacementValue(1L, 500.0)).willReturn(updated);

        mockMvc.perform(put("/api/tools/{id}/replacement-value", 1L)
                        .param("value", "500.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replacementValue").value(500.0));
    }

    @Test
    @DisplayName("DELETE /api/tools/{id} debe retornar 204 No Content")
    void deleteTool_returnsNoContent() throws Exception {
        doNothing().when(toolService).deleteToolById(1L);

        mockMvc.perform(delete("/api/tools/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/tools/{id}/image debe subir imagen")
    void uploadImage_returnsOk() throws Exception {
        ToolEntity tool = buildTool();
        given(toolService.getToolById(1L)).willReturn(tool);
        given(fileStorageService.store(any(), anyString())).willReturn("/public/img/taladro.png");
        given(toolService.saveTool(any(ToolEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "taladro.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
        );

        var mvcResult = mockMvc.perform(multipart("/api/tools/{id}/image", 1L)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagePath").value("/public/img/taladro.png"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("imagePath");
    }
}
