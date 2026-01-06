import httpClient from "../http-common";

const getLoanById = (id) => {
  return httpClient.get(`/loans-service/loans/${id}`);
};

const getAllLoans = () => {
  return httpClient.get("loans-service/loans");
};

const getActiveLoans = () => {
  return httpClient.get("loans-service/loans/active");
};

const getInactiveLoans = () => {
  return httpClient.get("loans-service/loans/inactive");
};

const getLoansByClientId = (clientId) => {
  return httpClient.get(`loans-service/loans/client/${clientId}`);
};

const getLoansByStartDate = (startDate) => {
  const formattedDate = startDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
  return httpClient.get(`/loans-service/loans/by-start-date?startDate=${formattedDate}`);
};

const getLoansByEndDate = (endDate) => {
  const formattedDate = endDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
  return httpClient.get(`/loans-service/loans/by-end-date?endDate=${formattedDate}`);
};

const getLoansByClientName = (name) => {
  return httpClient.get(`/loans-service/loans/by-client-name?name=${name}`);
};

const getLoansByClientRut = (rut) => {
  return httpClient.get(`/loans-service/loans/by-client-rut?rut=${rut}`);
};

const getActiveLoansByClientRut = (rut) => {
  return httpClient.get(`/loans-service/loans/active-by-rut?rut=${rut}`);
};

const createLoan = (loanData) => {
  return httpClient.post("/loans-service/loans", loanData);
};

const returnLoan = (loanId, payload) => {
  return httpClient.post(`/loans-service/loans/${loanId}/return`, payload);
};

const getLoansBeforeDate = (beforeDate) => {
  const formattedDate = beforeDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
  return httpClient.get(`/loans-service/loans/before-date?beforeDate=${formattedDate}`);
};

const searchLoans = (type, term) => {
  const params = type === 'name' ? { name: term } : { rut: term };
  return httpClient.get('/loans-service/loans/search', { params });
};

const remove = (id) => {
  return httpClient.delete(`/loans-service/loans/${id}`);
};

const getClientsWithFine = (fine) => {
  return httpClient.get(`/loans-service/loans/clients-with-fine/${fine}`);
};


export default {
  getLoanById,
  getClientsWithFine,
  getAllLoans,
  searchLoans,
  remove,
  getActiveLoans,
  getInactiveLoans,
  getLoansByClientId,
  getLoansByStartDate,
  getLoansByEndDate,
  getLoansByClientName,
  getLoansByClientRut,
  getActiveLoansByClientRut,
  createLoan,
  returnLoan,
  getLoansBeforeDate
};
