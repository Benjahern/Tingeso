import axios from "axios";
import keycloak from "./services/keycloak";

const toolRentBackendPort = import.meta.env.VITE_TOOLRENT_BACKEND;

const api = axios.create({
    baseURL: `http://localhost:80`,
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


