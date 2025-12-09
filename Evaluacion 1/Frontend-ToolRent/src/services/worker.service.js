import httpClient from "../http-common";

const getMe = () => {
    return httpClient.get("api/workers/me");
}

const getAllWorkers = () => {
    return httpClient.get("api/workers");
}

const getWorker = id => {
    return httpClient.get(`api/workers/${id}`);
}

const createWorker = data => {
    return httpClient.post("api/workers", data);
}

const getWorkersByStoreId = storeId => {
    return httpClient.get(`api/workers/store/${storeId}`);
} 

const updateWorker = (id, data) => {
    return httpClient.put(`api/workers/${id}`, data);
}

const deleteWorker = id => {
    return httpClient.delete(`api/workers/${id}`);
}

const getRolesByWorkerId = id => {
    return httpClient.get(`api/workers/${id}/roles`);
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