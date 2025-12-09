import httpClient from "../http-common";

const createLoanUnit = (loanUnitData) => {
	return httpClient.post(`/loan/unit`, loanUnitData);
};

export default {
	createLoanUnit,
};

