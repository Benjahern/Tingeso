package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import com.example.Backend_ToolRent.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkerService {

    private final WorkerRepository workerRepo;

    @Autowired
    private  KeycloakService keycloakService;

    public WorkerService(WorkerRepository workerRepo) {
        this.workerRepo = workerRepo;
    }

    @Transactional
    public WorkerEntity addWorker(WorkerEntity worker, String password) {
        try{
            System.out.println("=== DATOS DEL WORKER ===");
            System.out.println("Name: " + worker.getName());
            System.out.println("Email: " + worker.getMail());
            System.out.println("Password: " + (password != null ? "***" : "null"));
            System.out.println("Roles: " + worker.getRol());

            String fullName = worker.getName();
            String firstName = "";
            String lastName = "";

            if (fullName != null && !fullName.trim().isEmpty()) {
                String[] nameParts = fullName.trim().split("\\s+");
                firstName = nameParts[0];
                lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
            }

            String keycloakUserId = keycloakService.createUser(
                    worker.getMail(),
                    worker.getMail(),
                    password,
                    firstName,
                    lastName
            );

            List<String> roleNames = worker.getRol().stream().map(rol -> ((RolEntity) rol).getRolName()).collect(Collectors.toList());
            keycloakService.assignRoles(keycloakUserId, roleNames);

            worker.setKeycloakId(keycloakUserId);
            worker.setPassword(password);
            return workerRepo.save(worker);

        } catch (Exception e){
            throw new RuntimeException("Error al crear el trabajador: " + e.getMessage());
        }
    }

    public List<WorkerEntity> getAllWorkers() {
        return workerRepo.findAll();
    }

    public WorkerEntity getWorkerByMail(String mail) {
        return workerRepo.findByMail(mail).orElseThrow(() -> new EntityNotFoundException("Worker no encontrado"));
    }

    public WorkerEntity getWorkerById(Long id) {
        return workerRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Worker Not Found"));
    }

    public WorkerEntity getWorkerByName(String name) {
        return workerRepo.findByNameIgnoreCase(name).orElseThrow(()-> new EntityNotFoundException("Worker Not Found"));

    }

    @Transactional
    public WorkerEntity updateWorker(Long workerId, WorkerEntity updatedWorker) {
        WorkerEntity worker = workerRepo.findById(workerId).orElseThrow(() -> new RuntimeException("Worker not dound"));

        try {
            worker.setName(updatedWorker.getName());
            worker.setMail(updatedWorker.getMail());
            worker.setRol(updatedWorker.getRol());

            if (worker.getKeycloakId() != null) {
                List<String> roleName = updatedWorker.getRol().stream().map(rol -> rol.getRolName()).collect(Collectors.toList());

                keycloakService.updateRoles(worker.getKeycloakId(), roleName);
            }
            return workerRepo.save(worker);
        }catch (Exception e){
            throw new RuntimeException("Error al actualizar el trabajador: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteWorker(Long workerId) {
        WorkerEntity worker = workerRepo.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        try {
            // Eliminar de Keycloak
            if (worker.getKeycloakId() != null) {
                keycloakService.deleteUser(worker.getKeycloakId());
            }

            workerRepo.delete(worker);

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar trabajador: " + e.getMessage());
        }
    }

    public List<RolEntity> getRolByWorkerId(Long id) {
        if (workerRepo.existsById(id)) {
            WorkerEntity worker = getWorkerById(id);
            return worker.getRol();
        }else {
            throw new EntityNotFoundException("Worker Not Found");
        }
    }

    public List<WorkerEntity> getWorkerByStore(Long id) {
        return workerRepo.findByStore_StoreId(id);
    }

    public void deleteWorkerByStore(Long id) {
        workerRepo.deleteById(id);
    }

}
