package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.LoanUnitEntity;
import com.example.Backend_ToolRent.repository.LoanUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanUnitServiceTest {

    @Mock
    private LoanUnitRepository loanUnitRepo;

    @InjectMocks
    private LoanUnitService loanUnitService;

    @Test
    @DisplayName("getLoanUnitById devuelve entidad cuando existe")
    void getLoanUnitById_whenExists_returnsEntity() {
        LoanUnitEntity lu = new LoanUnitEntity();
        lu.setId(1L);

        given(loanUnitRepo.findById(1L)).willReturn(Optional.of(lu));

        LoanUnitEntity result = loanUnitService.getLoanUnitById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(loanUnitRepo).findById(1L);
    }

    @Test
    @DisplayName("getLoanUnitById lanza excepción cuando no existe")
    void getLoanUnitById_whenNotExists_throwsException() {
        given(loanUnitRepo.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> loanUnitService.getLoanUnitById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("LoanUnit no encontrado con ID: 1");

        verify(loanUnitRepo).findById(1L);
    }

    @Test
    @DisplayName("getHistoryForUnit delega en repo")
    void getHistoryForUnit_delegatesToRepository() {
        given(loanUnitRepo.findByUnit_UnitId(10L)).willReturn(List.of(new LoanUnitEntity()));

        List<?> result = loanUnitService.getHistoryForUnit(10L);

        assertThat(result).hasSize(1);
        verify(loanUnitRepo).findByUnit_UnitId(10L);
    }

    @Test
    @DisplayName("findReturnsByState delega en repo")
    void findReturnsByState_delegatesToRepository() {
        given(loanUnitRepo.findByState("Prestado")).willReturn(List.of(new LoanUnitEntity()));

        List<?> result = loanUnitService.findReturnsByState("Prestado");

        assertThat(result).hasSize(1);
        verify(loanUnitRepo).findByState("Prestado");
    }
}
