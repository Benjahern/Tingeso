import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get("api/tools");
}

const get = id => {
    return httpClient.get(`api/tools/${id}`);
}

const searchToolByName = (toolName) => {
  return httpClient.get(`/api/tools/search`, {
    params: { name: toolName }
  });
};

const create = data => {
    return httpClient.post("api/tools", data);
}

const updateTool = (toolId, toolDetails) => {
  return httpClient.put(`/api/tools/${toolId}`, toolDetails);
};

const setDailyPrice = (toolId, price) => {
  return httpClient.put(`/api/tools/${toolId}/daily-price`, null, {
    params: { price }
  });
};

const setReplacementValue = (toolId, value) => {
  return httpClient.put(`/api/tools/${toolId}/replacement-value`, null, {
    params: { value }
  });
}

const remove = id => {
    return httpClient.delete(`api/tools/${id}`);
}

export default {
    getAll,
    get,
    create,
    updateTool,
    remove,
    searchToolByName,
    setDailyPrice,
    setReplacementValue
};