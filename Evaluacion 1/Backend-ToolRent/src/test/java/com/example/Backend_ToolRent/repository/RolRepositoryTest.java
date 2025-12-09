package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.RolEntity;
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
class RolRepositoryTest {

    @Mock
    private RolRepository rolRepository;

    @Test
    @DisplayName("findByRolName devuelve rol mockeado")
    void findByRolName_mocked() {
        RolEntity rol = new RolEntity();
        rol.setRolName("ADMIN");

        given(rolRepository.findByRolName("ADMIN"))
                .willReturn(Optional.of(rol));

        Optional<RolEntity> result = rolRepository.findByRolName("ADMIN");

        assertThat(result).isPresent();
        assertThat(result.get().getRolName()).isEqualTo("ADMIN");
    }
}
