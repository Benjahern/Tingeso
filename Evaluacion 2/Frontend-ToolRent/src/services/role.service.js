import httpClient from "../http-common";

const getAllRoles = () => {
    return httpClient.get("users-service/roles");
};

export default {
    getAllRoles
};
