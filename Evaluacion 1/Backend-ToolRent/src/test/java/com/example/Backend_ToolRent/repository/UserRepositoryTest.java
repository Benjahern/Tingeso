package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.UserEntity;
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
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    // Subclase concreta para pruebas
    static class TestUser extends UserEntity {}

    @Test
    @DisplayName("findByMail devuelve user")
    void findByMail_returnsUser() {
        TestUser user = new TestUser();
        user.setMail("test@mail.com");

        given(userRepository.findByMail("test@mail.com")).willReturn(Optional.of(user));

        Optional<UserEntity> result = userRepository.findByMail("test@mail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getMail()).isEqualTo("test@mail.com");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase devuelve user")
    void findByName_returnsUser() {
        TestUser user = new TestUser();
        user.setName("Juan");

        given(userRepository.findByNameContainingIgnoreCase("juan")).willReturn(Optional.of(user));

        Optional<UserEntity> result = userRepository.findByNameContainingIgnoreCase("juan");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Juan");
    }
}
