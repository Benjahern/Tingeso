import React from 'react'
import './App.css'
import { Routes } from 'react-router-dom';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import HomePage from './components/HomePage';
import Sidebar from './components/Sidebar';
import { useKeycloak } from '@react-keycloak/web';


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
      <div className="container">
        <Sidebar />
        <Routes>
          <Route path="/home" element={<PrivateRoute element={<HomePage />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App
  