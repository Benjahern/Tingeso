import React from 'react'
import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './components/Pages/HomePage';
import ClientPage from './components/Pages/ClientPage';
import ClientAdd from './components/Pages/ClientAdd';
import Sidebar from './components/Pages/Sidebar';
import { useKeycloak } from '@react-keycloak/web';
import InventoryPage from './components/Pages/InventoryPage';
import ToolAdd from './components/Pages/ToolAdd';
import UnitPage from './components/Pages/UnitPage';
import UnitAddEditPage from './components/Pages/UnitAddEditPage';
import LoansPage from './components/Pages/LoansPage';
import ToolSelect from './components/Pages/ToolSelect';
import LoansAdd from './components/Pages/LoansAdd';
import LoanDetails from './components/Pages/LoanDetails';
import SettingsPage from './components/Pages/SettingsPage';
import WorkerPage from './components/Pages/WorkerPage';
import AddWorker from './components/Pages/AddWorker';
import ClientLoans from './components/Pages/ClientLoans';
import ToolHistory from './components/Pages/ToolHistory';
import History from  './components/Pages/History'


function App() {
  const { keycloak, initialized } = useKeycloak();

  if (!initialized) return <div>Loading...</div>;

  const isLoggedIn = keycloak.authenticated;
  const roles = keycloak.tokenParsed?.realm_access?.roles || [];

  const PrivateRoute = ({ element, rolesAllowed }) => {
    if (!isLoggedIn) {
      keycloak.login();
      return null;
    }

    if (rolesAllowed && !rolesAllowed.some(role => roles.includes(role))) {
      return <div>Access Denied</div>;
    }

    return element;
  };

  if (!isLoggedIn) {
    keycloak.login();
    return null;
  }

  return (
    <Router>
      <div className="d-flex">
        <Sidebar />
        <Routes>
          <Route path='/' element={<PrivateRoute element={<HomePage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/home" element={<PrivateRoute element={<HomePage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/clients" element={<PrivateRoute element={<ClientPage/>} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/clients/add/:id" element={<PrivateRoute element={<ClientAdd />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/clients/add" element={<PrivateRoute element={<ClientAdd />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          {/* <Route path="/loans" element={<PrivateRoute element={<LoansPage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} /> */}

          <Route path="/inventory" element={<PrivateRoute element={<InventoryPage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/tools/add" element={<PrivateRoute element={<ToolAdd />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/tools/add/:id" element={<PrivateRoute element={<ToolAdd />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/tools/:toolId" element={<PrivateRoute element={<UnitPage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/tools/:toolId/units/add" element={<PrivateRoute element={<UnitAddEditPage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/units/edit/:id" element={<PrivateRoute element={<UnitAddEditPage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/loans" element={<PrivateRoute element={<LoansPage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} /> 
          
          <Route path="/loans/add" element={<PrivateRoute element={<ToolSelect />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/loans/add/create" element={<PrivateRoute element={<LoansAdd />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/settings" element={<PrivateRoute element={<SettingsPage />} rolesAllowed={["ADMIN"]} />} />

          <Route path="/employees" element={<PrivateRoute element={<WorkerPage />} rolesAllowed={["ADMIN"]} />} />

          <Route path="/employees/add" element={<PrivateRoute element={<AddWorker />} rolesAllowed={["ADMIN"]} />} />
          
          <Route path="/loans/:loanId" element={<PrivateRoute element={<LoanDetails />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/tools/history/:toolId" element={<PrivateRoute element={<ToolHistory />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path='/history' element={<PrivateRoute element={<History />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />

          <Route path="/clients/:clientId/loans" element={<PrivateRoute element={<ClientLoans />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App
  