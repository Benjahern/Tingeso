package com.example.rates_service.Service;

import com.example.rates_service.Entity.StoreEntity;
import com.example.rates_service.Repository.StoreRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    private final StoreRepository storeRepo;

    public StoreService(StoreRepository storeRepo) {
        this.storeRepo = storeRepo;
    }

    // Initialize default store if none exists
    @PostConstruct
    public void initDefaultStore() {
        if (storeRepo.count() == 0) {
            StoreEntity defaultStore = new StoreEntity();
            defaultStore.setDailyFine(1000L); // Default daily fine: $1000
            storeRepo.save(defaultStore);
        }
    }

    public StoreEntity getStore() {
        return storeRepo.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
    }

    public Long getDailyFine() {
        return getStore().getDailyFine();
    }

    public StoreEntity updateDailyFine(Long dailyFine) {
        StoreEntity store = getStore();
        store.setDailyFine(dailyFine);
        return storeRepo.save(store);
    }
}
