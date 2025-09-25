import httpClient from "../http-common";

const getAllUnits = () => {
  return httpClient.get(`/api/units`);
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

export default {
  getAllUnits,
  getUnitById,
  getUnitsByToolId,
  searchUnits,
  createUnit
};