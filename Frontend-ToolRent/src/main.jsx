import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./services/keycloak";

const keycloakInitOptions = {
  onLoad: 'login-required',
  checkLoginIframe: false,
  flow: 'standard'
};

const keycloakProviderInitOptions = {
  initOptions: keycloakInitOptions,
};


ReactDOM.createRoot(document.getElementById('root')).render(
  <ReactKeycloakProvider 
    authClient={keycloak}>
    <App />
  </ReactKeycloakProvider>
)
