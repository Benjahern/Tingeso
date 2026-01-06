import httpClient from "../http-common";

const createClient = data => {
    return httpClient.post("clients-service/clients", data);
}

const getAllClients = (page = 1, size = 10) => {
    return httpClient.get(`clients-service/clients?page=${page}&size=${size}`);
}

const getClient = id => {
    return httpClient.get(`clients-service/clients/${id}`);
}

const searchClient = (searchType, query, page = 1, size = 10) => {
    return httpClient.get(`clients-service/clients/search?${searchType}=${query}&page=${page}&size=${size}`);
}

const updateClient = (id, client) => {
    return httpClient.put(`clients-service/clients/${id}`, client);
}

const deleteClient = id => {
    return httpClient.delete(`clients-service/clients/${id}`);
}

const clientWithDebts = () => {
    return httpClient.get("clients-service/clients/with-debt");
}

const addDebtToClient = (id, amount) => {
    return httpClient.post(`clients-service/clients/${id}/debt/add`, { amount });

}

const updateStateOfClient = (id, newState) => {
    return httpClient.post(`clients-service/clients/${id}/state`, { newState });
}

const payDebtForClient = (id, amount) => {
    return httpClient.post(`/clients-service/clients/${id}/debt/pay`, { amount });
};

const updateClientState = (id, newState) => {
    return httpClient.put(`/clients-service/clients/${id}/state`, { newState });
};


export default {
    createClient,
    getAllClients,
    getClient,
    updateClient,
    deleteClient,
    searchClient,
    clientWithDebts,
    addDebtToClient,
    updateStateOfClient,
    payDebtForClient,
    updateClientState
};