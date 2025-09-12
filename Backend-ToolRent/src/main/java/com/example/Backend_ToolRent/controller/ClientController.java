package com.example.Backend_ToolRent.controller;


import com.example.Backend_ToolRent.entity.ClientEntity;
import com.example.Backend_ToolRent.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin("*")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientEntity> createClient(@RequestBody ClientEntity client) {
        ClientEntity newClient = clientService.addClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(newClient);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientEntity>> getAllClients() {
        List<ClientEntity> clients = clientService.getAllClient();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientEntity> getClientById(@PathVariable Long id) {
        ClientEntity client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> searchClients(@RequestParam(required = false) String name, @RequestParam(required = false) String rut, @RequestParam(required = false) String state) {

        if (name != null) {
            return ResponseEntity.ok(clientService.getClientByName(name));
        }
        if (rut != null) {
            return ResponseEntity.ok(clientService.getClientsByRut(rut));
        }
        if (state != null) {
            return ResponseEntity.ok(clientService.getClientsByState(state));
        }
        return ResponseEntity.badRequest().body("Por favor, proporcione un parámetro de búsqueda (name, rut, o state).");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientEntity> updateClient(@PathVariable Long id, @RequestBody ClientEntity clientDetails) {
        clientDetails.setUserId(id); // Asegura que el ID sea el correcto
        ClientEntity updatedClient = clientService.updateClient(clientDetails);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClientById(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }

    @GetMapping("/with-debt")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientEntity>> getClientsWithDebt() {
        return ResponseEntity.ok(clientService.getClientsWithDebt());
    }

    @PostMapping("/{id}/debt/add")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientEntity> addDebtToClient(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(clientService.addDebt(id, amount));
    }

    @PostMapping("/{id}/debt/pay")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientEntity> payDebtForClient(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(clientService.payDebt(id, amount));
    }

    @PutMapping("/{id}/state")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientEntity> updateClientState(@PathVariable Long id, @RequestParam String newState) {
        return ResponseEntity.ok(clientService.setState(id, newState));
    }

}
