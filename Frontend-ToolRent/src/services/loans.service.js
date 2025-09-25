import httpClient from "../http-common";

const getLoanById = (id) => {
    return httpClient.get(`api/loans/${id}`);
}

const getAllLoans = () => {
    return httpClient.get("api/loans");
}

const getActiveLoans = () => {
    return httpClient.get("api/loans/active");
}

const getInactiveLoans = () => {
    return httpClient.get("api/loans/inactive");
}

const getLoansByClientId = (clientId) => {
    return httpClient.get(`api/loans/client/${clientId}`);
}

const getLoansByStartDate = (startDate) => {
    const formattedDate = startDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
    return httpClient.get(`/api/loans/by-start-date?startDate=${formattedDate}`);
};

const getLoansByEndDate = (endDate) => {
    const formattedDate = endDate.toISOString().slice(0, 10); // Formato YYYY-MM-DD
    return httpClient.get(`/api/loans/by-end-date?endDate=${formattedDate}`);
}

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
export default {
    getLoanById,
    getAllLoans,
    getActiveLoans,
    getInactiveLoans,
    getLoansByClientId,
    getLoansByStartDate,
    getLoansByEndDate,
    createLoan,
    returnLoan,
    getLoansBeforeDate
};
