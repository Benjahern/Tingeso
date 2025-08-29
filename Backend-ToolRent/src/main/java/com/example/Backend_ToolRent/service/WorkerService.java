package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.model.RolEntity;
import com.example.Backend_ToolRent.model.WorkerEntity;
import com.example.Backend_ToolRent.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {

    private final WorkerRepository workerRepo;

    public WorkerService(WorkerRepository workerRepo) {
        this.workerRepo = workerRepo;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public WorkerEntity addWorker(WorkerEntity workerEntity) {
        return workerRepo.save(workerEntity);
    }

    public List<WorkerEntity> getAllWorkers() {
        return workerRepo.findAll();
    }

    public WorkerEntity getWorkerById(Long id) {
        return workerRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Worker Not Found"));
    }

    public WorkerEntity getWorkerByName(String name) {
        return workerRepo.findByUserName(name).orElseThrow(()-> new EntityNotFoundException("Worker Not Found"));

    }

    public WorkerEntity updateWorker(WorkerEntity workerEntity) {
        return workerRepo.save(workerEntity);
    }
    public void deleteWorker(Long id) {
        workerRepo.deleteById(id);
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

}
