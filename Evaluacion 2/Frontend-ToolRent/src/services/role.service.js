import httpClient from "../http-common";

const getAllRoles = () => {
    return httpClient.get("api/roles");
};

export default {
    getAllRoles
};
