package com.example.Backend_ToolRent.service;

import java.util.List;
import java.util.Collections;
import com.example.Backend_ToolRent.entity.ClientEntity;
import com.example.Backend_ToolRent.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepo;

    @InjectMocks
    private ClientService clientService;

    private ClientEntity buildClient() {
        ClientEntity client = new ClientEntity();
        client.setUserId(1L);
        client.setName("Juan Perez");
        client.setDebt(1000.0);
        return client;
    }

    @Test
    @DisplayName("addDebt suma deuda correctamente")
    void addDebt_increasesDebt() {
        ClientEntity client = buildClient();
        given(clientRepo.existsById(1L)).willReturn(true);
        given(clientRepo.findById(1L)).willReturn(Optional.of(client));
        given(clientRepo.save(any(ClientEntity.class))).willAnswer(i -> i.getArgument(0));

        ClientEntity result = clientService.addDebt(1L, 500.0);

        assertThat(result.getDebt()).isEqualTo(1500.0);
    }

    @Test
    @DisplayName("payDebt reduce deuda correctamente")
    void payDebt_decreasesDebt() {
        ClientEntity client = buildClient(); // Deuda 1000
        given(clientRepo.existsById(1L)).willReturn(true);
        given(clientRepo.findById(1L)).willReturn(Optional.of(client));
        given(clientRepo.save(any(ClientEntity.class))).willAnswer(i -> i.getArgument(0));

        ClientEntity result = clientService.payDebt(1L, 400.0);

        assertThat(result.getDebt()).isEqualTo(600.0);
    }

    @Test
    @DisplayName("payDebt lanza excepcion si monto mayor a deuda")
    void payDebt_throwsIfAmountExceeds() {
        ClientEntity client = buildClient(); // Deuda 1000
        given(clientRepo.existsById(1L)).willReturn(true);
        given(clientRepo.findById(1L)).willReturn(Optional.of(client));

        assertThatThrownBy(() -> clientService.payDebt(1L, 2000.0))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Amount be less than the debt");
    }

    @Test
    @DisplayName("getClientById lanza excepcion si no existe")
    void getClientById_throwsException() {
        given(clientRepo.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("addClient guarda cliente exitosamente")
    void addClient_success() {
        ClientEntity client = buildClient();
        given(clientRepo.save(client)).willReturn(client);

        ClientEntity result = clientService.addClient(client);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Juan Perez");
        verify(clientRepo).save(client);
    }

    @Test
    @DisplayName("updateClient actualiza campos y guarda")
    void updateClient_success() {
        Long id = 1L;
        ClientEntity existingClient = buildClient();

        ClientEntity newDetails = new ClientEntity();
        newDetails.setName("Juan Updated");
        newDetails.setRut("99999999-9");
        newDetails.setMail("new@mail.com");
        newDetails.setPhone("123456789");
        newDetails.setAddress("New Address");
        newDetails.setState("RESTRINGIDO");
        newDetails.setDebt(5000.0);

        given(clientRepo.findById(id)).willReturn(Optional.of(existingClient));
        given(clientRepo.save(any(ClientEntity.class))).willAnswer(i -> i.getArgument(0));

        ClientEntity result = clientService.updateClient(id, newDetails);

        assertThat(result.getName()).isEqualTo("Juan Updated");
        assertThat(result.getRut()).isEqualTo("99999999-9");
        assertThat(result.getState()).isEqualTo("RESTRINGIDO");
        verify(clientRepo).save(existingClient);
    }

    @Test
    @DisplayName("updateClient lanza excepción si cliente no existe")
    void updateClient_throwsWhenNotFound() {
        given(clientRepo.findById(1L)).willReturn(Optional.empty());
        ClientEntity details = new ClientEntity();

        assertThatThrownBy(() -> clientService.updateClient(1L, details))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteClientById llama al repo")
    void deleteClientById_success() {
        clientService.deleteClientById(1L);
        verify(clientRepo).deleteById(1L);
    }

    @Test
    @DisplayName("addDebt lanza excepción si cliente no existe")
    void addDebt_throwsWhenClientNotFound() {
        given(clientRepo.existsById(1L)).willReturn(false);

        assertThatThrownBy(() -> clientService.addDebt(1L, 100.0))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("addDebt lanza excepción con monto negativo")
    void addDebt_throwsWhenAmountNegative() {
        given(clientRepo.existsById(1L)).willReturn(true);

        assertThatThrownBy(() -> clientService.addDebt(1L, -50.0))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Amount can't be less than 0");
    }

    @Test
    @DisplayName("payDebt lanza excepción con monto negativo")
    void payDebt_throwsWhenAmountNegative() {
        given(clientRepo.existsById(1L)).willReturn(true);

        assertThatThrownBy(() -> clientService.payDebt(1L, -100.0))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Amount can't be less than 0");
    }
    @Test
    @DisplayName("setState actualiza estado correctamente")
    void setState_success() {
        ClientEntity client = buildClient();
        given(clientRepo.existsById(1L)).willReturn(true);
        given(clientRepo.findById(1L)).willReturn(Optional.of(client));
        given(clientRepo.save(any(ClientEntity.class))).willAnswer(i -> i.getArgument(0));

        ClientEntity result = clientService.setState(1L, "BLOQUEADO");

        assertThat(result.getState()).isEqualTo("BLOQUEADO");
    }

    @Test
    @DisplayName("setState lanza excepción si cliente no existe")
    void setState_throwsWhenNotFound() {
        given(clientRepo.existsById(1L)).willReturn(false);

        assertThatThrownBy(() -> clientService.setState(1L, "ACTIVO"))
                .isInstanceOf(EntityNotFoundException.class);
    }
    @Test
    @DisplayName("Métodos de búsqueda delegan correctamente")
    void finderMethods_delegate() {
        // getClientsWithDebt
        clientService.getClientsWithDebt();
        verify(clientRepo).findByDebtGreaterThan(0);

        // getClientsByState
        clientService.getClientsByState("ACTIVO");
        verify(clientRepo).findByState("ACTIVO");

        // getClientByName
        clientService.getClientByName("Juan");
        verify(clientRepo).findByNameContainingIgnoreCase("Juan");

        // getClientsByRut
        clientService.getClientsByRut("123");
        verify(clientRepo).findByRut("123");

        // getAllClient
        clientService.getAllClient();
        verify(clientRepo).findAll();
    }




}
