import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./services/keycloak";

const keycloakInitOptions = {
  onLoad: 'login-required',
  checkLoginIframe: false,
  flow: 'standard',
  pkceMethod: 'S256'
};

// Event handlers for debugging
const onKeycloakEvent = (event, error) => {
  console.log('Keycloak event:', event);
  if (error) {
    console.error('Keycloak error:', error);
  }
};

const onKeycloakTokens = (tokens) => {
  console.log('Keycloak tokens received:', tokens ? 'yes' : 'no');
};

ReactDOM.createRoot(document.getElementById('root')).render(
  <ReactKeycloakProvider
    authClient={keycloak}
    initOptions={keycloakInitOptions}
    onEvent={onKeycloakEvent}
    onTokens={onKeycloakTokens}
    LoadingComponent={<div>Cargando Keycloak...</div>}
  >
    <App />
  </ReactKeycloakProvider>
)
