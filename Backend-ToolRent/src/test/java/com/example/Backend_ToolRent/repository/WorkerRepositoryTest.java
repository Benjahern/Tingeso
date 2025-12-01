package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.StoreEntity;
import com.example.Backend_ToolRent.entity.WorkerEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DataJpaTest

@ExtendWith(MockitoExtension.class)
class WorkerRepositoryTest {

    @Mock
    private WorkerRepository workerRepository;

    @Test
    @DisplayName("findByNameIgnoreCase devuelve worker")
    void findByNameIgnoreCase_returnsWorker() {
        WorkerEntity worker = new WorkerEntity();
        worker.setName("Pedro");

        given(workerRepository.findByNameIgnoreCase("PEDRO")).willReturn(Optional.of(worker));

        Optional<WorkerEntity> result = workerRepository.findByNameIgnoreCase("PEDRO");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Pedro");
    }

    @Test
    @DisplayName("findByStore_StoreId devuelve lista")
    void findByStoreId_returnsWorkers() {
        WorkerEntity worker = new WorkerEntity();
        StoreEntity store = new StoreEntity();
        store.setStoreId(10L);
        worker.setStore(store);

        given(workerRepository.findByStore_StoreId(10L)).willReturn(List.of(worker));

        List<WorkerEntity> result = workerRepository.findByStore_StoreId(10L);

        assertThat(result).hasSize(1);
    }
}
