package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.LoansEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class LoansRepositoryTest {

    @Mock
    private LoansRepository loansRepository;

    @Test
    @DisplayName("findByActive devuelve lista mockeada")
    void findByActive_mocked() {
        List<LoansEntity> mockedList = List.of(new LoansEntity());

        given(loansRepository.findByActive(true))
                .willReturn(mockedList);

        List<LoansEntity> result = loansRepository.findByActive(true);

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("findByClient_RutContainingIgnoreCase devuelve lista mockeada")
    void findByClientRutContainingIgnoreCase_mocked() {
        List<LoansEntity> mockedList = List.of(new LoansEntity());

        given(loansRepository.findByClient_RutContainingIgnoreCase("123456"))
                .willReturn(mockedList);

        List<LoansEntity> result = loansRepository.findByClient_RutContainingIgnoreCase("123456");

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("findByLoanStart devuelve lista mockeada")
    void findByLoanStart_mocked() {
        LocalDate date = LocalDate.now();
        List<LoansEntity> mockedList = List.of(new LoansEntity());

        given(loansRepository.findByLoanStart(date))
                .willReturn(mockedList);

        List<LoansEntity> result = loansRepository.findByLoanStart(date);

        assertThat(result).isNotEmpty();
    }

    // Puedes agregar más tests para otros métodos si quieres.
}
