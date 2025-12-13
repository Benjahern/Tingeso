import httpClient from "../http-common";

const createClient = data => {
    return httpClient.post("api/clients", data);
}

const getAllClients = (page = 1, size = 10) => {
    return httpClient.get(`api/clients?page=${page}&size=${size}`);
}

const getClient = id => {
    return httpClient.get(`api/clients/${id}`);
}

const searchClient = (searchType, query, page = 1, size = 10) => {
    return httpClient.get(`api/clients/search?${searchType}=${query}&page=${page}&size=${size}`);
}

const updateClient = (id, client) => {
    return httpClient.put(`api/clients/${id}`, client);
}

const deleteClient = id => {
    return httpClient.delete(`api/clients/${id}`);
}

const clientWithDebts = () => {
    return httpClient.get("api/clients/with-debt");
}

const addDebtToClient = (id, amount) => {
    return httpClient.post(`api/clients/${id}/debt/add`, { amount });

}

const updateStateOfClient = (id, newState) => {
    return httpClient.post(`api/clients/${id}/state`, { newState });
}

const payDebtForClient = (id, amount) => {
  return httpClient.post(`/api/clients/${id}/debt/pay`, { amount });
};

const updateClientState = (id, newState) => {
  return httpClient.put(`/api/clients/${id}/state`, { newState });
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