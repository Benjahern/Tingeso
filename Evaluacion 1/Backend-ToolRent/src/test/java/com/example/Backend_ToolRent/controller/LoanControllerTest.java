package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.ClientEntity;
import com.example.Backend_ToolRent.entity.LoansEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.service.LoansService;
import com.example.Backend_ToolRent.service.WorkerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoansService loansService;

    @MockitoBean
    private WorkerService workerService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private LoansEntity buildLoan() {
        LoansEntity loan = new LoansEntity();
        loan.setLoanId(1L);
        loan.setLoanStart(LocalDate.of(2025, 1, 1));
        loan.setLoanEnd(LocalDate.of(2025, 1, 5));
        loan.setPrice(100.0);
        loan.setActive(true);
        return loan;
    }

    private WorkerEntity buildWorker() {
        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(99L);
        worker.setMail("test@example.com");
        return worker;
    }

    private ClientEntity buildClient() {
        ClientEntity client = new ClientEntity();
        client.setUserId(1L);
        client.setName("Juan Perez");
        client.setRut("12345678-9");
        return client;
    }

    @Test
    @DisplayName("GET /api/loans/{id} devuelve préstamo por id")
    void getLoanById_returnsOk() throws Exception {
        given(loansService.getLoansById(1L)).willReturn(buildLoan());

        mockMvc.perform(get("/api/loans/{id}", 1L)
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/active devuelve préstamos activos")
    void getActiveLoans_returnsOk() throws Exception {
        given(loansService.getLoansByActive()).willReturn(List.of(buildLoan()));

        mockMvc.perform(get("/api/loans/active")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans devuelve todos los préstamos")
    void getAllLoans_returnsOk() throws Exception {
        given(loansService.getAllLoans()).willReturn(List.of(buildLoan()));

        mockMvc.perform(get("/api/loans")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/loans/{id} elimina préstamo")
    void removeLoanById_returnsOkMessage() throws Exception {
        doNothing().when(loansService).removeLoansById(1L);

        mockMvc.perform(delete("/api/loans/{id}", 1L)
                        .with(jwt().authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prestamo eliminado exitosamente"));
    }

    @Test
    @DisplayName("GET /api/loans/search busca por nombre")
    void searchLoan_withName_returnsOk() throws Exception {
        given(loansService.getLoansByClientName("Juan")).willReturn(List.of(buildLoan()));

        mockMvc.perform(get("/api/loans/search")
                        .param("name", "Juan")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/search devuelve bad request si no hay params")
    void searchLoan_noParams_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/loans/search")
                        .with(jwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/loans crea préstamo exitosamente (JWT con Email)")
    void createLoan_success() throws Exception {
        given(workerService.getWorkerByMail("test@example.com")).willReturn(buildWorker());
        given(loansService.createLoan(anyLong(), anyLong(), any(), any(), anyList(), anyLong()))
                .willReturn(buildLoan());

        String jsonBody = """
                {
                    "clientId": 1,
                    "storeId": 2,
                    "startDate": "2025-01-01",
                    "endDate": "2025-01-05",
                    "toolIds": [10, 20]
                }
                """;

        mockMvc.perform(post("/api/loans")
                        .with(jwt().jwt(jwt -> jwt.claim("email", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loanId").value(1L));
    }

    @Test
    @DisplayName("POST /api/loans maneja excepción y devuelve 500")
    void createLoan_exception_returns500() throws Exception {
        given(workerService.getWorkerByMail(anyString())).willThrow(new RuntimeException("DB Error"));

        String jsonBody = """
                { "clientId": 1, "storeId": 2, "startDate": "2025-01-01", "endDate": "2025-01-05", "toolIds": [] }
                """;

        mockMvc.perform(post("/api/loans")
                        .with(jwt().jwt(jwt -> jwt.claim("email", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/loans/{id}/return devuelve préstamo exitosamente")
    void returnLoan_success() throws Exception {
        given(workerService.getWorkerByMail("test@example.com")).willReturn(buildWorker());
        given(loansService.returnLoan(eq(1L), eq(99L), anyMap())).willReturn(buildLoan());

        String jsonBody = "{\"10\": \"good\"}";

        mockMvc.perform(post("/api/loans/{id}/return", 1L)
                        .with(jwt().jwt(jwt -> jwt.claim("email", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(1L));
    }

    // --- Nuevos Tests para Cobertura del 100% ---

    @Test
    @DisplayName("GET /api/loans/inactive devuelve préstamos inactivos")
    void getInactiveLoans_returnsOk() throws Exception {
        given(loansService.getLoansByInactive()).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/inactive").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/client/{clientId} devuelve préstamos por cliente")
    void getAllLoansByClientId_returnsOk() throws Exception {
        given(loansService.getLoansByClientId(1L)).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/client/{clientId}", 1L).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/by-start-date devuelve préstamos por fecha de inicio")
    void getLoanByStartDate_returnsOk() throws Exception {
        given(loansService.getByLoanStart(any(LocalDate.class))).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/by-start-date")
                        .param("startDate", "2025-01-01")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/by-end-date devuelve préstamos por fecha de fin")
    void getLoanByEndDate_returnsOk() throws Exception {
        given(loansService.getByLoanEnd(any(LocalDate.class))).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/by-end-date")
                        .param("endDate", "2025-01-05")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/before-date devuelve préstamos activos antes de la fecha")
    void getLoanBeforeDate_returnsOk() throws Exception {
        given(loansService.getActiveBeforeDate(any(LocalDate.class))).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/before-date")
                        .param("beforeDate", "2025-01-10")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/by-client-rut devuelve préstamos por RUT")
    void getLoansByClientRut_returnsOk() throws Exception {
        given(loansService.getLoansByClientRut("12345678-9")).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/by-client-rut")
                        .param("rut", "12345678-9")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/active-by-rut devuelve préstamos activos por RUT")
    void getActiveLoansByClientRut_returnsOk() throws Exception {
        given(loansService.getActiveLoansByClientRut("12345678-9")).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/active-by-rut")
                        .param("rut", "12345678-9")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/search busca por RUT")
    void searchLoan_withRut_returnsOk() throws Exception {
        given(loansService.getLoansByClientRut("12345678-9")).willReturn(List.of(buildLoan()));
        mockMvc.perform(get("/api/loans/search")
                        .param("rut", "12345678-9")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1L));
    }

    @Test
    @DisplayName("GET /api/loans/clientfine/{fine} devuelve clientes con multas")
    void getClientsWithFine_returnsOk() throws Exception {
        given(loansService.getClientsWithFine(100L)).willReturn(List.of(buildClient()));
        mockMvc.perform(get("/api/loans/clientfine/{fine}", 100L).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan Perez"));
    }

    @Test
    @DisplayName("POST /api/loans/{id}/return maneja excepción y devuelve 400")
    void returnLoan_exception_returnsBadRequest() throws Exception {
        given(workerService.getWorkerByMail(anyString())).willReturn(buildWorker());
        given(loansService.returnLoan(anyLong(), anyLong(), anyMap())).willThrow(new RuntimeException("Error en la devolución"));

        String jsonBody = "{\"10\": \"broken\"}";

        mockMvc.perform(post("/api/loans/{id}/return", 1L)
                        .with(jwt().jwt(jwt -> jwt.claim("email", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error en la devolución"));
    }
}

