package Controller;

import Entity.WorkerEntity;
import Service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerEntity> addWorker(@RequestBody Map<String, Object> request) {
        WorkerEntity worker = new WorkerEntity();
        worker.setName((String) request.get("name"));
        worker.setMail((String) request.get("mail"));

        Object rolesObj = request.get("rol");
        if (rolesObj instanceof List) {
            List<?> rolesList = (List<?>) rolesObj;
            // Cast seguro a Set<String> si los elementos son String
            Set<String> roles = rolesList.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
            worker.setRol(roles);
        } else {
            worker.setRol(new HashSet<>());
        }


        String password = (String) request.get("password");

        WorkerEntity created = workerService.addWorker(worker, password);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<WorkerEntity>> getAllWorkers() {
        List<WorkerEntity> workers = workerService.getAllWorkers();
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<WorkerEntity> getWorkerById(@PathVariable Long id) {
        WorkerEntity worker = workerService.getWorkerById(id);
        return ResponseEntity.ok(worker);
    }

    @GetMapping("/mail")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<WorkerEntity> getWorkerByMail(@RequestParam String mail) {
        WorkerEntity worker = workerService.getWorkerByMail(mail);
        return ResponseEntity.ok(worker);
    }

    @GetMapping("/name")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<WorkerEntity> getWorkerByName(@RequestParam String name) {
        WorkerEntity worker = workerService.getWorkerByName(name);
        return ResponseEntity.ok(worker);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerEntity> updateWorker(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        WorkerEntity worker = new WorkerEntity();
        worker.setName((String) request.get("name"));
        worker.setMail((String) request.get("mail"));

        Object rolesObj = request.get("rol");
        if (rolesObj instanceof List) {
            List<?> rolesList = (List<?>) rolesObj;
            // Cast seguro a Set<String> si los elementos son String
            Set<String> roles = rolesList.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
            worker.setRol(roles);
        } else {
            worker.setRol(new HashSet<>());
        }
        // -------------------------------

        WorkerEntity updated = workerService.updateWorker(id, worker);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }





}
