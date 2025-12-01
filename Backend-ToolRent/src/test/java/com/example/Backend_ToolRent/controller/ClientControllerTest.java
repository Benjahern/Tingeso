package com.example.Backend_ToolRent.controller;

import java.util.Collections;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.example.Backend_ToolRent.entity.ClientEntity;
import com.example.Backend_ToolRent.service.ClientService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClientController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    private ClientEntity buildClient() {
        ClientEntity client = new ClientEntity();
        client.setUserId(1L);
        client.setName("Juan Perez");
        client.setRut("12345678-9");
        return client;
    }

    @Test
    @DisplayName("GET /api/clients/{id} devuelve cliente")
    void getClientById_returnsOk() throws Exception {
        given(clientService.getClientById(1L)).willReturn(buildClient());

        mockMvc.perform(get("/api/clients/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    @DisplayName("POST /api/clients crea cliente")
    void createClient_returnsCreated() throws Exception {
        ClientEntity input = buildClient();
        given(clientService.addClient(any(ClientEntity.class))).willReturn(input);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    @DisplayName("POST /api/clients/{id}/debt/add agrega deuda")
    void addDebt_returnsOk() throws Exception {
        ClientEntity updated = buildClient();
        updated.setDebt(5000.0);
        given(clientService.addDebt(anyLong(), anyDouble())).willReturn(updated);

        Map<String, Double> body = Map.of("amount", 5000.0);

        mockMvc.perform(post("/api/clients/{id}/debt/add", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debt").value(5000.0));
    }

    @Test
    @DisplayName("GET /api/clients/search busca por nombre")
    void searchClients_byName() throws Exception {
        given(clientService.getClientByName("Juan")).willReturn(List.of(buildClient()));

        mockMvc.perform(get("/api/clients/search")
                        .param("name", "Juan")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/clients devuelve lista de clientes")
    void getAllClients_returnsOk() throws Exception {
        given(clientService.getAllClient()).willReturn(List.of(buildClient()));

        mockMvc.perform(get("/api/clients")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan Perez"));
    }

    @Test
    @DisplayName("PUT /api/clients/{id} actualiza cliente")
    void updateClient_returnsOk() throws Exception {
        ClientEntity updated = buildClient();
        updated.setName("Juan Updated");

        given(clientService.updateClient(eq(1L), any(ClientEntity.class))).willReturn(updated);

        mockMvc.perform(put("/api/clients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Updated"));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id} elimina cliente")
    void deleteClient_returnsNoContent() throws Exception {
        doNothing().when(clientService).deleteClientById(1L);

        mockMvc.perform(delete("/api/clients/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(clientService).deleteClientById(1L);
    }

    @Test
    @DisplayName("POST /api/clients/{id}/debt/pay paga deuda")
    void payDebt_returnsOk() throws Exception {
        ClientEntity client = buildClient();
        client.setDebt(0.0);

        given(clientService.payDebt(anyLong(), anyDouble())).willReturn(client);

        Map<String, Double> body = Map.of("amount", 1000.0);

        mockMvc.perform(post("/api/clients/{id}/debt/pay", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debt").value(0.0));
    }

    @Test
    @DisplayName("GET /api/clients/with-debt devuelve deudores")
    void getClientsWithDebt_returnsOk() throws Exception {
        ClientEntity debtor = buildClient();
        debtor.setDebt(500.0);

        given(clientService.getClientsWithDebt()).willReturn(List.of(debtor));

        mockMvc.perform(get("/api/clients/with-debt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].debt").value(500.0));
    }

    @Test
    @DisplayName("GET /api/clients/search busca por rut")
    void searchClients_byRut() throws Exception {
        given(clientService.getClientsByRut("123")).willReturn(List.of(buildClient()));

        mockMvc.perform(get("/api/clients/search")
                        .param("rut", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rut").value("12345678-9"));
    }

    @Test
    @DisplayName("GET /api/clients/search busca por estado")
    void searchClients_byState() throws Exception {
        ClientEntity activeClient = buildClient();
        activeClient.setState("ACTIVO");

        given(clientService.getClientsByState("ACTIVO")).willReturn(List.of(activeClient));

        mockMvc.perform(get("/api/clients/search")
                        .param("state", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("ACTIVO"));
    }

    @Test
    @DisplayName("GET /api/clients/search retorna Bad Request sin params")
    void searchClients_badRequest() throws Exception {
        mockMvc.perform(get("/api/clients/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/clients/{id}/state actualiza estado")
    void updateClientState_returnsOk() throws Exception {
        ClientEntity client = buildClient();
        client.setState("BLOQUEADO");

        given(clientService.setState(eq(1L), anyString())).willReturn(client);

        Map<String, String> body = Map.of("state", "BLOQUEADO");

        mockMvc.perform(put("/api/clients/{id}/state", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("BLOQUEADO"));
    }

}
