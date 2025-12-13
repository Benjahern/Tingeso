import httpClient from "../http-common";

const getAllKardex = () => {
    return httpClient.get("api/kardex/getAll");
}

const getKardexByUnitId = (unitId) => {
    return httpClient.get(`api/kardex/history/unit/${unitId}`);
} 

const getKardexByToolId = (toolId) => {
    return httpClient.get(`api/kardex/history/tool/${toolId}`);
}

const getRankingHerramientas = (params) => {
  return httpClient.get("api/kardex/ranking", { params });
};


export default {
    getAllKardex,
    getRankingHerramientas,
    getKardexByUnitId,
    getKardexByToolId
};
