package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.LoanUnitEntity;
import com.example.Backend_ToolRent.service.LoanUnitService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoanUnitController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class LoanUnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoanUnitService loanUnitService;

    private LoanUnitEntity buildLoanUnit() {
        LoanUnitEntity lu = new LoanUnitEntity();
        lu.setId(1L);
        lu.setState("Prestado");
        return lu;
    }

    @Test
    @DisplayName("POST /loan/unit crea una entidad")
    void createLoanUnit_returnsEntity() throws Exception {
        LoanUnitEntity input = buildLoanUnit();
        given(loanUnitService.createLoanUnit(any(LoanUnitEntity.class))).willReturn(input);

        mockMvc.perform(post("/loan/unit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
