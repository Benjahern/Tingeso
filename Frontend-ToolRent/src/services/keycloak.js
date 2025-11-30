import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: "http://127.0.0.1:8080",
  realm: "ToolRent-realm",
  clientId: "toolrent-frontend",
});

export default keycloak;