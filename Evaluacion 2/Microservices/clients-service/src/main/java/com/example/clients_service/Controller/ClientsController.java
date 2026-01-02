package com.example.clients_service.Controller;

import com.example.clients_service.Entity.ClientsEntity;
import com.example.clients_service.Service.ClientsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clients")
public class ClientsController {

    private final ClientsService clientsService;

    public ClientsController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientsEntity> createClient(@RequestBody ClientsEntity clientsEntity) {
        ClientsEntity createdClient = clientsService.createClient(clientsEntity);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientsService.deleteClientById(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientsEntity> updateClient(@PathVariable Long id, @RequestBody ClientsEntity clientDetails) {
        ClientsEntity updatedClient = clientsService.updateClient(id, clientDetails);
        return ResponseEntity.ok(updatedClient);
    }

    @PostMapping("/{id}/debt/add")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientsEntity> addDebtToClient(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        Double amount = body.get("amount");
        return ResponseEntity.ok(clientsService.addDebt(id, amount));
    }

    @PostMapping("/{id}/debt/pay")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientsEntity> payDebtForClient(@PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        Double amount = body.get("amount");
        return ResponseEntity.ok(clientsService.payDebt(id, amount));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientsEntity>> getAllClients() {
        List<ClientsEntity> clients = clientsService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ClientsEntity> getClientById(@PathVariable Long id) {
        ClientsEntity client = clientsService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> searchClients(@RequestParam(required = false) String name,
            @RequestParam(required = false) String rut, @RequestParam(required = false) String state) {

        if (name != null) {
            List<ClientsEntity> clients = clientsService.getClientByName(name);
            return ResponseEntity.ok(clients);
        }
        if (rut != null) {
            List<ClientsEntity> clients = clientsService.getClientsByRut(rut);
            return ResponseEntity.ok(clients);
        }
        if (state != null) {
            List<ClientsEntity> clients = clientsService.getClientsByState(state);
            return ResponseEntity.ok(clients);
        }
        return ResponseEntity.badRequest()
                .body("Por favor, proporcione un parámetro de búsqueda (name, rut, o state).");
    }

    @GetMapping("/name")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientsEntity>> getAllClientByName(@RequestParam String name) {
        List<ClientsEntity> clients = clientsService.getClientByName(name);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/rut")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientsEntity>> getClientByRut(@RequestParam String rut) {
        List<ClientsEntity> clients = clientsService.getClientsByRut(rut);
        return ResponseEntity.ok(clients);

    }

    @GetMapping("/with-debt")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ClientsEntity>> getClientsWithDebt() {
        return ResponseEntity.ok(clientsService.getClientsWithDebt());
    }

    @PostMapping("/{id}/state")
    public ResponseEntity<ClientsEntity> setState(@PathVariable Long id, @RequestParam String state) {
        clientsService.setState(id, state);
        return ResponseEntity.ok(clientsService.getClientById(id));
    }

}
