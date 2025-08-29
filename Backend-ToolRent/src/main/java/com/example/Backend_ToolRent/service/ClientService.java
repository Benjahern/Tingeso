package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.model.ClientEntity;
import com.example.Backend_ToolRent.model.LoansEntity;
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


    public ClientEntity getClientByName(String name) {
        return clientRepo.findByUserName(name).orElseThrow(()-> new EntityNotFoundException("Client with name: "+ name + " not found"));
    }

    @Transactional
    public ClientEntity updateClient(ClientEntity client) {
        return clientRepo.save(client);
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

    public ClientEntity getClientsByRut(String rut){
        return clientRepo.findByRut(rut).orElseThrow(()-> new EntityNotFoundException("Client with id: "+ rut+ " not found"));
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
