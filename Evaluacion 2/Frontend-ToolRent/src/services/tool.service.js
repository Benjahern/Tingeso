import httpClient from "../http-common";

const getAll = () => {
  return httpClient.get("inventory-service/tools");
}

const get = id => {
  return httpClient.get(`inventory-service/tools/${id}`);
}

const searchToolByName = (toolName) => {
  return httpClient.get(`/inventory-service/tools/search`, {
    params: { name: toolName }
  });
};

const create = data => {
  return httpClient.post("inventory-service/tools", data);
}

const updateTool = (toolId, toolDetails) => {
  return httpClient.put(`/inventory-service/tools/${toolId}`, toolDetails);
};

const setDailyPrice = (toolId, price) => {
  return httpClient.put(`/inventory-service/tools/${toolId}/daily-price`, null, {
    params: { price }
  });
};

const setReplacementValue = (toolId, value) => {
  return httpClient.put(`/inventory-service/tools/${toolId}/replacement-value`, null, {
    params: { value }
  });
};

const remove = id => {
  return httpClient.delete(`inventory-service/tools/${id}`);
};

const uploadImage = (toolId, imageFile) => {
  const formData = new FormData();
  formData.append("file", imageFile);
  return httpClient.post(`/inventory-service/tools/${toolId}/image`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    }
  })
};

export default {
  getAll,
  get,
  create,
  updateTool,
  remove,
  searchToolByName,
  setDailyPrice,
  setReplacementValue,
  uploadImage
};