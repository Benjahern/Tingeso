import axios from "axios";
import keycloak from "./services/keycloak";

// URL del Gateway desde variable de entorno
const gatewayUrl = import.meta.env.VITE_GATEWAY_URL;

const api = axios.create({
    baseURL: gatewayUrl,
    headers: {
        "Content-Type": "application/json"
    }
});

api.interceptors.request.use(async (config) => {
    console.log(`HTTP Request: ${config.method.toUpperCase()} ${config.url}`, config.data);
    if (keycloak.authenticated && keycloak.token) {
        try {
            await keycloak.updateToken(30);
        } catch (error) {
            console.warn("Token refresh failed (likely due to insecure context), proceeding with existing token.", error);
        }
        config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;
