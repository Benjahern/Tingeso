import httpClient from "../http-common";

const getStoreById = (id) => {
  return httpClient.get(`rates-service/store/${id}`);
}

const updateDailyFine = (storeId, newFine) => {
  return httpClient.put(`/rates-service/store/${storeId}/daily-fine`, { newFine });
};


export default {
  getStoreById,
  updateDailyFine
};