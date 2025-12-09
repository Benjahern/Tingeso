package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.ClientEntity;
import com.example.Backend_ToolRent.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClientService {

    private final ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepo) {
        this.clientRepo = clientRepo;
    }

    @Transactional
    public ClientEntity addClient(ClientEntity client) {
        return clientRepo.save(client);
    }

    public List<ClientEntity> getAllClient() {
        return clientRepo.findAll();
    }

    public ClientEntity getClientById(Long id) {
        return clientRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Client with id: "+ id+ " not found"));
    }


    public List<ClientEntity> getClientByName(String name) {
        return clientRepo.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public ClientEntity updateClient(Long id, ClientEntity clientDetails) {
        // 1. Busca el cliente existente en la base de datos
        ClientEntity existingClient = clientRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Client with id: "+ id + " not found"));

        // 2. Actualiza los campos del cliente existente con los nuevos datos
        existingClient.setName(clientDetails.getName());
        existingClient.setRut(clientDetails.getRut());
        existingClient.setMail(clientDetails.getMail());
        existingClient.setPhone(clientDetails.getPhone());
        existingClient.setAddress(clientDetails.getAddress());
        existingClient.setState(clientDetails.getState());
        existingClient.setDebt(clientDetails.getDebt());

        // 3. Guarda el cliente actualizado en la base de datos
        return clientRepo.save(existingClient);
    }

    @Transactional
    public void deleteClientById(Long id) {
        clientRepo.deleteById(id);
    }

    @Transactional
    public ClientEntity addDebt(Long id, Double amount){
        if(!clientRepo.existsById(id)){
            throw new EntityNotFoundException("Client with id: "+ id + " not found");

        }else{
            if(amount <= 0){
                throw new EntityNotFoundException("Amount can't be less than 0");
            }
            ClientEntity client = getClientById(id);
            client.setDebt(amount+client.getDebt());
            return clientRepo.save(client);
        }
    }

    public List<ClientEntity> getClientsWithDebt(){
        return clientRepo.findByDebtGreaterThan(0);
    }

    public List<ClientEntity> getClientsByState(String state){
        return clientRepo.findByState(state);
    }

    @Transactional
    public ClientEntity payDebt(Long id, Double amount){
        if(clientRepo.existsById(id)){
            if(amount <= 0){
                throw new EntityNotFoundException("Amount can't be less than 0");
            }
            ClientEntity client = getClientById(id);
            if(client.getDebt() >= amount){
                client.setDebt(client.getDebt()-amount);
                return clientRepo.save(client);
            }else {
                throw new EntityNotFoundException("Amount be less than the debt");
            }
        }
        throw new EntityNotFoundException("Client with id: "+ id + " not found");
    }

    public List<ClientEntity> getClientsByRut(String rut){
        return clientRepo.findByRut(rut);
    }

    @Transactional
    public ClientEntity setState(Long id,String state){
        if(clientRepo.existsById(id)){
            ClientEntity client = getClientById(id);
            client.setState(state);
            return clientRepo.save(client);
        }
        throw new EntityNotFoundException("Client with id: "+ id + " not found");
    }

}
