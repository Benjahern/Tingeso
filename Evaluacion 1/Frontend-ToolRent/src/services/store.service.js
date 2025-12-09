import httpClient from "../http-common";

const getStoreById = (id) => {
    return httpClient.get(`api/stores/${id}`);
}

const updateDailyFine = (storeId, newFine) => {
  return httpClient.put(`/api/stores/${storeId}/daily-fine`, { newFine });
};


export default {
    getStoreById,
    updateDailyFine
};