package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.StoreEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DataJpaTest

@ExtendWith(MockitoExtension.class)
class StoreRepositoryTest {

    @Mock
    private StoreRepository storeRepository;

    @Test
    @DisplayName("findById devuelve store mockeada")
    void findById_returnsStore() {
        StoreEntity store = new StoreEntity();
        store.setStoreId(1L);

        given(storeRepository.findById(1L)).willReturn(Optional.of(store));

        Optional<StoreEntity> result = storeRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getStoreId()).isEqualTo(1L);
    }
}
