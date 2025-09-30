import httpClient from "../http-common";

const createClient = data => {
    return httpClient.post("api/clients", data);
}

const getAllClients = () => {
    return httpClient.get("api/clients");
}

const getClient = id => {
    return httpClient.get(`api/clients/${id}`);
}

const searchClient = (searchType, query) => {
    return httpClient.get(`api/clients/search?${searchType}=${query}`);
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