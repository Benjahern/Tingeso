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

export default {
    getAllKardex,
    getKardexByUnitId,
    getKardexByToolId
};
