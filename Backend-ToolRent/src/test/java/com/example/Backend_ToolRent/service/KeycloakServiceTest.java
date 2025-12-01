package com.example.Backend_ToolRent.service;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

    // @Spy es vital aquí: crea una instancia real de KeycloakService pero nos deja
    // "espiar" y modificar métodos específicos (como getKeycloakInstance)
    @InjectMocks
    @Spy
    private KeycloakService keycloakService;

    // --- Mocks de la jerarquía de Keycloak ---
    // Necesitamos un mock para cada paso de la cadena: keycloak.realm().users().get()...
    @Mock private Keycloak keycloakMock;
    @Mock private RealmResource realmResource;
    @Mock private UsersResource usersResource;
    @Mock private UserResource userResource;
    @Mock private RolesResource rolesResource;
    @Mock private RoleResource roleResource;
    @Mock private RoleMappingResource roleMappingResource;
    @Mock private RoleScopeResource roleScopeResource;

    @BeforeEach
    void setUp() {
        // 1. Inyectamos las propiedades @Value que Spring inyectaría normalmente
        ReflectionTestUtils.setField(keycloakService, "realm", "test-realm");

        // 2. "Secuestramos" el método que crea el cliente real para devolver nuestro mock.
        // Esto evita que intente conectarse a Internet/localhost real.
        // REQUIERE: protected Keycloak getKeycloakInstance() en la clase original
        doReturn(keycloakMock).when(keycloakService).getKeycloakInstance();
    }

    @Test
    @DisplayName("createUser crea usuario y devuelve ID cuando Keycloak responde 201")
    void createUser_success() throws Exception {
        // Preparar respuesta fake de Keycloak (éxito)
        Response responseSuccess = Response.status(201)
                .location(new URI("http://localhost/auth/admin/realms/test/users/user-uuid-123"))
                .build();

        // Configurar la cadena de llamadas
        // keycloak.realm("test-realm") -> devuelve realmResource
        when(keycloakMock.realm(anyString())).thenReturn(realmResource);
        // realmResource.users() -> devuelve usersResource
        when(realmResource.users()).thenReturn(usersResource);
        // usersResource.create(...) -> devuelve nuestra respuesta fake
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(responseSuccess);

        // Para el resetPassword: usersResource.get(id) -> userResource
        when(usersResource.get("user-uuid-123")).thenReturn(userResource);

        // Ejecutar
        String userId = keycloakService.createUser("jdoe", "jdoe@test.com", "123456", "John", "Doe");

        // Verificar
        assertThat(userId).isEqualTo("user-uuid-123");
        verify(userResource).resetPassword(any()); // Verifica que asignó contraseña
    }

    @Test
    @DisplayName("assignRoles busca roles y los asigna al usuario")
    void assignRoles_success() {
        // Configurar cadena para llegar al usuario
        when(keycloakMock.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get("user-123")).thenReturn(userResource);

        // Configurar cadena para buscar roles disponibles
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get("ADMIN")).thenReturn(roleResource);

        RoleRepresentation adminRole = new RoleRepresentation();
        adminRole.setName("ADMIN");
        when(roleResource.toRepresentation()).thenReturn(adminRole);

        // Configurar cadena para asignar roles
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);

        // Ejecutar
        keycloakService.assignRoles("user-123", List.of("ADMIN"));

        // Verificar que se llamó al método final .add() con una lista que contiene el rol
        verify(roleScopeResource).add(argThat(list ->
                list.size() == 1 && list.get(0).getName().equals("ADMIN")
        ));
    }

    @Test
    @DisplayName("deleteUser llama a remove() en el recurso de usuario")
    void deleteUser_success() {
        // Configurar cadena
        when(keycloakMock.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get("user-to-delete")).thenReturn(userResource);

        // Ejecutar
        keycloakService.deleteUser("user-to-delete");

        // Verificar
        verify(userResource).remove();
    }
}
