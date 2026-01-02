package com.example.clients_service.Service;

import com.example.clients_service.Entity.ClientsEntity;
import com.example.clients_service.Repository.ClientsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ClientsService {

    private final ClientsRepository clientsRepo;

    public ClientsService(ClientsRepository clientsRepo) {
        this.clientsRepo = clientsRepo;
    }

    @Transactional
    public ClientsEntity createClient(ClientsEntity client) {
        return clientsRepo.save(client);
    }

    @Transactional
    public void deleteClientById(Long id) {
        if (!clientsRepo.existsById(id)) {
            log.error("Client with id {} not found. Delete failed.", id);
            throw new EntityNotFoundException("Client with id: " + id + " not found");
        }
        try {
            clientsRepo.deleteById(id);
            log.info("Client with id {} deleted successfully.", id);
        } catch (Exception e) {
            log.error("Failed to delete client with id {}: {}", id, e.getMessage(), e);
            throw e;
        }

    }

    @Transactional
    public ClientsEntity updateClient(Long id, ClientsEntity client) {
        if (!clientsRepo.existsById(id)) {
            log.error("Client with id {} not found. Update failed.", id);
        }
        try {
            ClientsEntity clientEntity = clientsRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));

            clientEntity.setName(client.getName());
            clientEntity.setRut(client.getRut());
            clientEntity.setMail(client.getMail());
            clientEntity.setDebt(client.getDebt());
            clientEntity.setPhone(client.getPhone());
            clientEntity.setState(client.getState());

            return clientsRepo.save(clientEntity);
        } catch (Exception e) {
            log.error("Failed to update client with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public ClientsEntity addDebt(Long id, Double amount) {
        if (!clientsRepo.existsById(id)) {
            throw new EntityNotFoundException("Client with id " + id + " not found. Add debt failed.");
        } else {
            if (amount <= 0) {
                throw new RuntimeException("Amount must be greater than 0");

            }
            ClientsEntity client = getClientById(id);
            client.setDebt(amount + client.getDebt());
            return clientsRepo.save(client);
        }
    }

    @Transactional
    public ClientsEntity payDebt(Long id, Double amount) {
        if (clientsRepo.existsById(id)) {
            if (amount <= 0) {
                throw new RuntimeException("Amount can't be less than 0");
            }
            ClientsEntity client = getClientById(id);
            if (client.getDebt() >= amount) {
                client.setDebt(client.getDebt() - amount);
                return clientsRepo.save(client);
            } else {
                throw new RuntimeException("Amount be less than the debt");
            }
        }
        throw new EntityNotFoundException("Client with id: " + id + " not found");
    }

    @Transactional
    public ClientsEntity setState(Long id, String state) {
        if (clientsRepo.existsById(id)) {
            ClientsEntity client = getClientById(id);
            client.setState(state);
            return clientsRepo.save(client);
        }
        throw new EntityNotFoundException("Client with id: " + id + " not found");
    }

    /*
     * Seccion de getters
     */

    public List<ClientsEntity> getAllClients() {
        return clientsRepo.findAll();
    }

    public ClientsEntity getClientById(Long id) {
        return clientsRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    public List<ClientsEntity> getClientByName(String name) {
        return clientsRepo.findByNameContainingIgnoreCase(name);
    }

    public List<ClientsEntity> getClientsWithDebt() {
        return clientsRepo.findByDebtGreaterThan(0);
    }

    public List<ClientsEntity> getClientsByState(String state) {
        return clientsRepo.findByState(state);
    }

    public List<ClientsEntity> getClientsByRut(String rut) {
        return clientsRepo.findByRut(rut);
    }

}
