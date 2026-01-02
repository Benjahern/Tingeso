
package com.example.users_service.Service;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Value("${keycloak.admin-client-id}")
    private String clientId;

    protected Keycloak getKeycloakInstance() {
        System.out.println("=== Conectando a Keycloak ===");
        System.out.println("Server: " + serverUrl);
        System.out.println("Realm auth: master");
        System.out.println("Client: " + clientId);
        System.out.println("Username: " + adminUsername);

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    public String createUser(String username, String email, String password,
            String firstName, String lastName) {
        Keycloak keycloak = getKeycloakInstance();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        Response response = usersResource.create(user);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Status Info: " + response.getStatusInfo());

        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath()
                    .replaceAll(".*/users/", "");

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            usersResource.get(userId).resetPassword(credential);

            return userId;
        } else {
            String errorBody = "";
            try {
                errorBody = response.readEntity(String.class);
            } catch (Exception e) {
                errorBody = "No se pudo leer el error";
            }

            System.err.println("✗ Error creando usuario:");
            System.err.println("Status: " + response.getStatus());
            System.err.println("Error body: " + errorBody);

            throw new RuntimeException("Error al crear usuario en Keycloak. Status: "
                    + response.getStatus() + " - " + errorBody);
        }
    }

    public void assignRoles(String userId, List<String> roleNames) {
        try {
            Keycloak keycloak = getKeycloakInstance();
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);

            List<RoleRepresentation> roles = new ArrayList<>();

            for (String roleName : roleNames) {
                try {
                    RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
                    roles.add(role);
                    System.out.println("✓ Rol encontrado: " + roleName);
                } catch (Exception e) {
                    System.err.println("✗ Rol no encontrado en Keycloak: " + roleName);
                    System.err.println("   Crea el rol '" + roleName + "' en Keycloak Admin Console");
                }
            }

            if (!roles.isEmpty()) {
                userResource.roles().realmLevel().add(roles);
                System.out.println("✓ Roles asignados correctamente: " + roleNames);
            } else {
                System.err.println("✗ No se pudieron asignar roles porque no existen en Keycloak");
            }

        } catch (Exception e) {
            System.err.println("✗ Error asignando roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateRoles(String userId, List<String> newRoleNames) {
        Keycloak keycloak = getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(realm);

        List<RoleRepresentation> currentRoles = realmResource.users()
                .get(userId)
                .roles()
                .realmLevel()
                .listAll();

        if (!currentRoles.isEmpty()) {
            realmResource.users().get(userId).roles().realmLevel().remove(currentRoles);
        }

        List<RoleRepresentation> newRoles = newRoleNames.stream()
                .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                .collect(Collectors.toList());

        realmResource.users().get(userId).roles().realmLevel().add(newRoles);
    }

    public void deleteUser(String userId) {
        Keycloak keycloak = getKeycloakInstance();
        keycloak.realm(realm).users().get(userId).remove();
    }

}
