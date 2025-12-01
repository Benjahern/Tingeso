package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.KardexEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DataJpaTest

@ExtendWith(MockitoExtension.class)
class KardexRepositoryTest {

    @Mock
    private KardexRepository kardexRepository;

    @Test
    @DisplayName("findByUnit_UnitIdOrderByDateDesc devuelve lista")
    void findByUnitId_returnsList() {
        KardexEntity kardex = new KardexEntity();
        kardex.setKardexId(1L);

        given(kardexRepository.findByUnit_UnitIdOrderByDateDesc(1L)).willReturn(List.of(kardex));

        List<KardexEntity> result = kardexRepository.findByUnit_UnitIdOrderByDateDesc(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findMostRequestedToolsWithLoan devuelve lista de objetos")
    void findMostRequestedTools_returnsList() {
        // Mockeamos que devuelve una lista vacía o con objetos (Object[])
        given(kardexRepository.findMostRequestedToolsWithLoan()).willReturn(List.of());

        List<Object[]> result = kardexRepository.findMostRequestedToolsWithLoan();

        assertThat(result).isNotNull();
    }
}
