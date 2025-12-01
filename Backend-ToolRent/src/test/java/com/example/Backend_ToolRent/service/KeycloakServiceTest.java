package com.example.Backend_ToolRent.service;

import jakarta.ws.rs.ProcessingException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

    @InjectMocks
    @Spy
    private KeycloakService keycloakService;

    // --- Mocks de Keycloak ---
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
        ReflectionTestUtils.setField(keycloakService, "realm", "test-realm");
        doReturn(keycloakMock).when(keycloakService).getKeycloakInstance();
    }

    // --- Tests: Create User ---

    @Test
    @DisplayName("createUser: Éxito (Status 201)")
    void createUser_success() throws Exception {
        Response responseSuccess = Response.status(201)
                .location(new URI("http://localhost/auth/admin/realms/test/users/user-uuid-123"))
                .build();

        when(keycloakMock.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(responseSuccess);
        when(usersResource.get("user-uuid-123")).thenReturn(userResource);

        String userId = keycloakService.createUser("jdoe", "jdoe@test.com", "123456", "John", "Doe");

        assertThat(userId).isEqualTo("user-uuid-123");
        verify(userResource).resetPassword(any());
    }

    @Test
    @DisplayName("createUser: Falla (Status 409 Conflict) con mensaje de error legible")
    void createUser_failure_readableError() {
        Response responseError = mock(Response.class);
        when(responseError.getStatus()).thenReturn(409);
        when(responseError.readEntity(String.class)).thenReturn("User exists");

        when(keycloakMock.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(responseError);

        assertThatThrownBy(() -> keycloakService.createUser("jdoe", "email", "pass", "J", "D"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al crear usuario en Keycloak. Status: 409 - User exists");
    }

    @Test
    @DisplayName("createUser: Falla y no se puede leer el error (Exception al leer entity)")
    void createUser_failure_unreadableError() {
        Response responseError = mock(Response.class);
        when(responseError.getStatus()).thenReturn(500);
        when(responseError.readEntity(String.class)).thenThrow(new RuntimeException("IO Error"));

        when(keycloakMock.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(responseError);

        assertThatThrownBy(() -> keycloakService.createUser("jdoe", "email", "pass", "J", "D"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pudo leer el error");
    }

    // --- Tests: Assign Roles ---

    @Test
    @DisplayName("assignRoles: Éxito total (encuentra todos los roles)")
    void assignRoles_success() {
        setupKeycloakHierarchyForRoles();

        // Mock del rol encontrado
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("ADMIN");
        when(rolesResource.get("ADMIN")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(roleRep);

        keycloakService.assignRoles("user-123", List.of("ADMIN"));

        verify(roleScopeResource).add(anyList());
    }

    @Test
    @DisplayName("assignRoles: Rol no encontrado (captura excepción y sigue)")
    void assignRoles_roleNotFound() {
        setupKeycloakHierarchyForRoles();

        // Simular que buscar el rol lanza excepción
        when(rolesResource.get("UNKNOWN")).thenThrow(new RuntimeException("Not found"));

        keycloakService.assignRoles("user-123", List.of("UNKNOWN"));

        // Verificar que NO se llamó a add() porque la lista de roles validos quedó vacía
        verify(roleScopeResource, never()).add(anyList());
    }

    @Test
    @DisplayName("assignRoles: Error general (Keycloak caído)")
    void assignRoles_generalError() {
        when(keycloakMock.realm(anyString())).thenThrow(new RuntimeException("Connection refused"));

        // El método captura la excepción e imprime el stacktrace, no lanza nada hacia afuera
        keycloakService.assignRoles("user-123", List.of("ADMIN"));

        // No hay aserción porque el método es void y captura todo, pero cubrimos el catch
    }

    // --- Tests: Update Roles ---

    @Test
    @DisplayName("updateRoles: Borra roles viejos y agrega nuevos")
    void updateRoles_success() {
        setupKeycloakHierarchyForRoles();

        // Simular roles actuales
        RoleRepresentation oldRole = new RoleRepresentation(); oldRole.setName("OLD");
        when(roleScopeResource.listAll()).thenReturn(List.of(oldRole));

        // Simular nuevo rol
        RoleRepresentation newRole = new RoleRepresentation(); newRole.setName("NEW");
        when(rolesResource.get("NEW")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(newRole);

        keycloakService.updateRoles("user-123", List.of("NEW"));

        verify(roleScopeResource).remove(anyList()); // Borró viejos
        verify(roleScopeResource).add(anyList());    // Agregó nuevos
    }

    @Test
    @DisplayName("updateRoles: No borra nada si no tiene roles actuales")
    void updateRoles_noCurrentRoles() {
        setupKeycloakHierarchyForRoles();
        when(roleScopeResource.listAll()).thenReturn(List.of()); // Lista vacía

        RoleRepresentation newRole = new RoleRepresentation(); newRole.setName("NEW");
        when(rolesResource.get("NEW")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(newRole);

        keycloakService.updateRoles("user-123", List.of("NEW"));

        verify(roleScopeResource, never()).remove(anyList()); // No debe llamar a remove
        verify(roleScopeResource).add(anyList());
    }

    // --- Tests: Delete User ---

    @Test
    @DisplayName("deleteUser: Éxito")
    void deleteUser_success() {
        when(keycloakMock.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get("user-to-delete")).thenReturn(userResource);

        keycloakService.deleteUser("user-to-delete");

        verify(userResource).remove();
    }

    // --- Helper ---
    private void setupKeycloakHierarchyForRoles() {
        when(keycloakMock.realm(anyString())).thenReturn(realmResource);

        // Ruta para obtener roles disponibles (realm.roles())
        lenient().when(realmResource.roles()).thenReturn(rolesResource);

        // Ruta para asignar roles al usuario (realm.users().get().roles().realmLevel())
        lenient().when(realmResource.users()).thenReturn(usersResource);
        lenient().when(usersResource.get(anyString())).thenReturn(userResource);
        lenient().when(userResource.roles()).thenReturn(roleMappingResource);
        lenient().when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
    }

}
