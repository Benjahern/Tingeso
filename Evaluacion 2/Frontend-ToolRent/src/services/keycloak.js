import Keycloak from "keycloak-js";

// URL de Keycloak desde variable de entorno
const keycloakUrl = import.meta.env.VITE_KEYCLOAK_URL;

const keycloak = new Keycloak({
  url: keycloakUrl,
  realm: "tingeso",
  clientId: "frontend-client",
});

export default keycloak;