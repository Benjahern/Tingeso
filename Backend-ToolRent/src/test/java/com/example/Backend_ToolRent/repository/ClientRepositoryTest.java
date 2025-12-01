package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.ClientEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DataJpaTest

@ExtendWith(MockitoExtension.class)
class ClientRepositoryTest {

    @Mock
    private ClientRepository clientRepository;

    @Test
    @DisplayName("findByDebtGreaterThan devuelve deudores")
    void findByDebtGreaterThan_returnsClients() {
        ClientEntity client = new ClientEntity();
        client.setDebt(5000.0);

        given(clientRepository.findByDebtGreaterThan(0.0))
                .willReturn(List.of(client));

        List<ClientEntity> result = clientRepository.findByDebtGreaterThan(0.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDebt()).isEqualTo(5000.0);
    }

    @Test
    @DisplayName("findByState devuelve clientes por estado")
    void findByState_returnsClients() {
        ClientEntity client = new ClientEntity();
        client.setState("Habilitado");

        given(clientRepository.findByState("Habilitado"))
                .willReturn(List.of(client));

        List<ClientEntity> result = clientRepository.findByState("Habilitado");

        assertThat(result).isNotEmpty();
    }
}
