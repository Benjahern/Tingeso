package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.UnitEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DataJpaTest

@ExtendWith(MockitoExtension.class)
class UnitRepositoryTest {

    @Mock
    private UnitRepository unitRepository;

    @Test
    @DisplayName("findByStatus devuelve lista mockeada")
    void findByStatus_mocked() {
        List<UnitEntity> mockedList = List.of(new UnitEntity());

        given(unitRepository.findByStatus("Disponible"))
                .willReturn(mockedList);

        List<UnitEntity> result = unitRepository.findByStatus("Disponible");

        assertThat(result).isNotEmpty();
    }
}
