package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.StoreEntity;
import com.example.Backend_ToolRent.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private StoreEntity buildStore() {
        StoreEntity store = new StoreEntity();
        store.setStoreId(1L);
        store.setStoreName("Tienda Central");
        store.setDailyFine(1000);
        return store;
    }

    @Test
    @DisplayName("saveStore delega en repositorio")
    void saveStore_delegates() {
        StoreEntity store = buildStore();
        given(storeRepository.save(any(StoreEntity.class))).willReturn(store);

        StoreEntity result = storeService.saveStore(store);

        assertThat(result.getStoreName()).isEqualTo("Tienda Central");
        verify(storeRepository).save(store);
    }

    @Test
    @DisplayName("getStoreById devuelve store si existe")
    void getStoreById_returnsStore() {
        given(storeRepository.findById(1L)).willReturn(Optional.of(buildStore()));

        StoreEntity result = storeService.getStoreById(1L);

        assertThat(result.getStoreId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getStoreById lanza EntityNotFoundException si no existe")
    void getStoreById_throwsException() {
        given(storeRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.getStoreById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Store with id 1 not found");
    }

    @Test
    @DisplayName("updateDailyFine actualiza y guarda")
    void updateDailyFine_updatesAndSaves() {
        StoreEntity store = buildStore(); // fine inicial 1000
        given(storeRepository.findById(1L)).willReturn(Optional.of(store));
        given(storeRepository.save(any(StoreEntity.class))).willAnswer(i -> i.getArgument(0));

        StoreEntity result = storeService.updateDailyFine(1L, 2000);

        assertThat(result.getDailyFine()).isEqualTo(2000);
        verify(storeRepository).save(store);
    }
}
