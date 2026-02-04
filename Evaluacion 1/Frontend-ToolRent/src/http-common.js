import axios from "axios";
import keycloak from "./services/keycloak";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:80/api",
    headers: {
        "Content-Type": "application/json"
    }
});

api.interceptors.request.use(async (config) => {
    if (keycloak.authenticated) {
        await keycloak.updateToken(30);
        config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;


