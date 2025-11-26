import httpClient from "../http-common";

const getAllUnits = () => {
  return httpClient.get(`/api/units`);
};

const updateUnit = (id, unitData) => {
  return httpClient.put(`/api/units/${id}`, unitData);
};

const getUnitById = (id) => {
  return httpClient.get(`/api/units/${id}`);
};

const getUnitsByToolId = (toolId) => {
  return httpClient.get(`/api/units/by-tool/${toolId}`);
};

const searchUnits = (filters) => {
  return httpClient.get(`/api/units/search`, { params: filters });
};

const createUnit = (unitData) => {
  return httpClient.post(`/api/units`, unitData);
};

const decommisionUnit = (unitId, data) => {
  return httpClient.post(`/api/units/${unitId}/decommission`, data);
};

const updateUnitStatus = (id, statusData) => {
  return httpClient.patch(`api/units/${id}/status`, statusData);
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