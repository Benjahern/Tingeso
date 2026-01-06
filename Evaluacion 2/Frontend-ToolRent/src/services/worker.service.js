import httpClient from "../http-common";

const getMe = () => {
    return httpClient.get("users-service/workers/me");
}

const getAllWorkers = () => {
    return httpClient.get("users-service/workers");
}

const getWorker = id => {
    return httpClient.get(`users-service/workers/${id}`);
}

const createWorker = data => {
    return httpClient.post("users-service/workers", data);
}

const getWorkersByStoreId = storeId => {
    return httpClient.get(`users-service/workers/store/${storeId}`);
}

const updateWorker = (id, data) => {
    return httpClient.put(`users-service/workers/${id}`, data);
}

const deleteWorker = id => {
    return httpClient.delete(`users-service/workers/${id}`);
}

const getRolesByWorkerId = id => {
    return httpClient.get(`users-service/workers/${id}/roles`);
}
export default {
    getMe,
    getAllWorkers,
    getWorker,
    createWorker,
    getWorkersByStoreId,
    updateWorker,
    deleteWorker,
    getRolesByWorkerId
};