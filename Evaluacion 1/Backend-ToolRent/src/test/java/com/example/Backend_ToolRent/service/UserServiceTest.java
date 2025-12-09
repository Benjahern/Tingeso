package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.UserEntity;
import com.example.Backend_ToolRent.repository.UserRepository;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // Subclase concreta para pruebas
    static class TestUser extends UserEntity {}

    @Test
    @DisplayName("getUserById devuelve user si existe")
    void getUserById_returnsUser() {
        TestUser user = new TestUser();
        user.setUserId(1L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        UserEntity result = userService.getUserById(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getUserById lanza excepción si no existe")
    void getUserById_throwsException() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("getUserByEmail devuelve user")
    void getUserByEmail_returnsUser() {
        TestUser user = new TestUser();
        user.setMail("a@b.com");

        given(userRepository.findByMail("a@b.com")).willReturn(Optional.of(user));

        UserEntity result = userService.getUserByEmail("a@b.com");

        assertThat(result.getMail()).isEqualTo("a@b.com");
    }

    @Test
    @DisplayName("getUserByName devuelve user")
    void getUserByName_returnsUser() {
        TestUser user = new TestUser();
        user.setName("Carlos");

        given(userRepository.findByNameContainingIgnoreCase("Carlos")).willReturn(Optional.of(user));

        UserEntity result = userService.getUserByName("Carlos");

        assertThat(result.getName()).isEqualTo("Carlos");
    }
}
