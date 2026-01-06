import httpClient from "../http-common";

const getAllUnits = () => {
  return httpClient.get(`/inventory-service/units`);
};

const updateUnit = (id, unitData) => {
  return httpClient.put(`/inventory-service/units/${id}`, unitData);
};

const getUnitById = (id) => {
  return httpClient.get(`/inventory-service/units/${id}`);
};

const getUnitsByToolId = (toolId) => {
  return httpClient.get(`/inventory-service/units/by-tool/${toolId}`);
};

const searchUnits = (filters) => {
  return httpClient.get(`/inventory-service/units/search`, { params: filters });
};

const createUnit = (unitData) => {
  return httpClient.post(`/inventory-service/units`, unitData);
};

const decommisionUnit = (unitId, data) => {
  return httpClient.post(`/inventory-service/units/${unitId}/decommission`, data);
};

const updateUnitStatus = (id, statusData) => {
  return httpClient.patch(`inventory-service/units/${id}/status`, statusData);
};

export default {
  getAllUnits,
  updateUnit,
  updateUnitStatus,
  getUnitById,
  getUnitsByToolId,
  decommisionUnit,
  searchUnits,
  createUnit
};