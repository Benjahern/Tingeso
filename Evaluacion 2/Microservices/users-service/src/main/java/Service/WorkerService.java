package Service;

import Entity.WorkerEntity;
import Repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class WorkerService {

    private final WorkerRepository workerRepo;

    private final KeycloakService keycloakService;

    public WorkerService(WorkerRepository workerRepo, KeycloakService keycloakService) {
        this.workerRepo = workerRepo;
        this.keycloakService = keycloakService;
    }

    @Transactional
    public WorkerEntity addWorker(WorkerEntity worker, String password) {
        try {
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
                    lastName);

            Set<String> roleSet = worker.getRol();
            List<String> roleNames = new ArrayList<>(roleSet); // Convertir Set a List
            keycloakService.assignRoles(keycloakUserId, roleNames);

            worker.setKeycloakId(keycloakUserId);
            worker.setPassword(password);
            return workerRepo.save(worker);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear el trabajador: " + e.getMessage());
        }
    }

    public List<WorkerEntity> getAllWorkers() {
        return workerRepo.findAll();
    }

    public WorkerEntity getWorkerById(Long id) {
        return workerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("No existe el usuario con el id: " + id));
    }

    public WorkerEntity getWorkerByMail(String mail) {
        return workerRepo.findByMail(mail).orElseThrow(() -> new EntityNotFoundException("No existe el usuario con el mail: " + mail));
    }

    public WorkerEntity getWorkerByName(String name) {
        return workerRepo.findByNameIgnoreCase(name).orElseThrow(() -> new EntityNotFoundException("No existe el usuario con el nombre: " + name));
    }

    public WorkerEntity updateWorker(Long id, WorkerEntity updatedWorker) {
        WorkerEntity worker = getWorkerById(id);

        try {
            worker.setName(updatedWorker.getName());
            worker.setMail(updatedWorker.getMail());
            worker.setRol(updatedWorker.getRol());

            if (worker.getKeycloakId() != null) {
                Set<String> roleNames = updatedWorker.getRol();
                List<String> roleName = new ArrayList<>(roleNames);

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



}
