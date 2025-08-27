package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.model.StoreEntity;
import com.example.Backend_ToolRent.repository.StoreRepository;
import org.springframework.security.access.prepost.PreAuthorize;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    /**
     * Dependency
     */
    private final StoreRepository storeRepository;

    /**
     * Constructor
     * @param storeRepository
     */
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    /**
     * Create or save a store
     * @param storeEntity
     * @return
     */
    @Transactional
    public StoreEntity saveStore(StoreEntity storeEntity) {
        return storeRepository.save(storeEntity);
    }

    /**
     * Function to get the store by Id
     * @param id
     * @return
     */
    public StoreEntity getStoreById(Long id) {
        return storeRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Store with id " + id + " not found"));
    }

    /**
     * Function to edit the daily charge
     * @param storeId
     * @param newFine
     * @return
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public StoreEntity updateDailyFine(Long storeId, int newFine) {
        StoreEntity storeUpdated = getStoreById(storeId);
        storeUpdated.setDailyFine(newFine);
        return storeRepository.save(storeUpdated);
    }

}
