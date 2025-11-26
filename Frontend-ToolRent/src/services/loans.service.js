import httpClient from "../http-common";

const getLoanById = (id) => {
  return httpClient.get(`/api/loans/${id}`);
};

const getAllLoans = () => {
  return httpClient.get("api/loans");
};

const getActiveLoans = () => {
  return httpClient.get("api/loans/active");
};

const getInactiveLoans = () => {
  return httpClient.get("api/loans/inactive");
};

const getLoansByClientId = (clientId) => {
  return httpClient.get(`api/loans/client/${clientId}`);
};

const getLoansByStartDate = (startDate) => {
  const formattedDate = startDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
  return httpClient.get(`/api/loans/by-start-date?startDate=${formattedDate}`);
};

const getLoansByEndDate = (endDate) => {
  const formattedDate = endDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
  return httpClient.get(`/api/loans/by-end-date?endDate=${formattedDate}`);
};

const getLoansByClientName = (name) => {
  return httpClient.get(`/api/loans/by-client-name?name=${name}`);
};

const getLoansByClientRut = (rut) => {
  return httpClient.get(`/api/loans/by-client-rut?rut=${rut}`);
};

const getActiveLoansByClientRut = (rut) => {
  return httpClient.get(`/api/loans/active-by-rut?rut=${rut}`);
};

const createLoan = (loanData) => {
  return httpClient.post("/api/loans", loanData);
};

const returnLoan = (loanId, unitConditions) => {
  return httpClient.post(`/api/loans/${loanId}/return`, unitConditions);
};

const getLoansBeforeDate = (beforeDate) => {
  const formattedDate = beforeDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
  return httpClient.get(`/api/loans/before-date?beforeDate=${formattedDate}`);
};

const searchLoans = (type, term) => {
    const params = type === 'name' ? { name: term } : { rut: term };
    return httpClient.get('/api/loans/search', { params });
};

const remove = (id) => {
  return httpClient.delete(`/api/loans/${id}`);
};

const getClientsWithFine = (fine) => {
  return httpClient.get(`/api/loans/clientfine/${fine}`);
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
