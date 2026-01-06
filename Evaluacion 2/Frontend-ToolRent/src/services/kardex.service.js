import httpClient from "../http-common";

const getAllKardex = () => {
    return httpClient.get("kardex-service/kardex");
}

const getKardexByUnitId = (unitId) => {
    return httpClient.get(`kardex-service/kardex/unit/${unitId}`);
}

const getKardexByToolId = (toolId) => {
    return httpClient.get(`kardex-service/kardex/tool/${toolId}`);
}

const getRankingHerramientas = (params) => {
    return httpClient.get("reports-service/reports/ranking", { params });
};


export default {
    getAllKardex,
    getRankingHerramientas,
    getKardexByUnitId,
    getKardexByToolId
};
