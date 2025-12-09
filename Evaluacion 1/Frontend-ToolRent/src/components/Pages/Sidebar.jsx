import React from "react";
import { Link, useLocation, NavLink } from "react-router-dom"; 
import { useKeycloak } from '@react-keycloak/web'; 
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

const Sidebar = () => {
    const {keycloak} = useKeycloak();
    const location = useLocation();

    const handleLogout = () => {
        keycloak.logout();
    };
    
    const userName = keycloak.tokenParsed?.preferred_username || "prueba";

    // Función para verificar si la ruta está activa
    const isActiveRoute = (path) => {
        return location.pathname === path;
    };

    // Array con las opciones de navegación principales
    const navigationItems = [
        { path: '/home', label: 'Home', icon: 'bi-house' },
        { path: '/inventory', label: 'Inventory', icon: 'bi-box' },
        { path: '/loans', label: 'Loans', icon: 'bi-arrow-left-right' },
        { path: '/clients', label: 'Clients', icon: 'bi-people' },
        { path: '/employees', label: 'Employees', icon: 'bi-person-badge' }
    ];

    return (
        <div className="d-flex">
            {/* Sidebar */}
            <div 
                className="bg-dark text-white position-fixed h-100 d-flex flex-column"
                style={{ 
                    width: '280px', 
                    top: 0, 
                    left: 0,
                    zIndex: 1000
                }}
            >
                {/* Header */}
                <div className="p-4 border-bottom border-secondary">
                    <h2 className="h4 mb-0 fw-bold">ToolRent</h2>
                </div>

                {/* Navigation */}
                <nav className="flex-grow-1 py-3">
                    <ul className="nav nav-pills flex-column px-3">
                        {navigationItems.map((item) => (
                            <li key={item.path} className="nav-item mb-2">
                                <NavLink 
                                    to={item.path}
                                    className={({ isActive }) => 
                                        `nav-link text-white d-flex align-items-center px-3 py-2 rounded ${
                                            isActive ? 'bg-secondary' : ''
                                        }`
                                    }
                                    style={({ isActive }) => ({
                                        backgroundColor: isActive ? '#6c757d' : 'transparent',
                                        transition: 'background-color 0.2s ease'
                                    })}
                                >
                                    <i className={`${item.icon} me-3`} style={{ fontSize: '1.1rem' }}></i>
                                    {item.label}
                                </NavLink>
                            </li>
                        ))}
                    </ul>
                </nav>

                {/* Footer */}
                <div className="mt-auto p-3 border-top border-secondary">
                    {/* Usuario */}
                    <div className="px-3 py-2">
                        <small className="text-uppercase text-muted fw-semibold" style={{ fontSize: '0.75rem', letterSpacing: '0.5px' }}>
                            {userName}
                        </small>
                    </div>

                    {/* Settings */}
                    <NavLink 
                        to="/settings"
                        className={({ isActive }) => 
                            `nav-link text-white d-flex align-items-center px-3 py-2 rounded mb-2 ${
                                isActive ? 'bg-secondary' : ''
                            }`
                        }
                    >
                        <i className="bi-gear me-3" style={{ fontSize: '1.1rem' }}></i>
                        Settings
                    </NavLink>

                    {/* Logout */}
                    <button 
                        onClick={handleLogout}
                        className="btn text-white d-flex align-items-center px-3 py-2 rounded w-100 border-0"
                        style={{ 
                            backgroundColor: 'transparent',
                            transition: 'background-color 0.2s ease'
                        }}
                        onMouseEnter={(e) => e.target.style.backgroundColor = '#dc3545'}
                        onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                    >
                        <i className="bi-box-arrow-right me-3" style={{ fontSize: '1.1rem' }}></i>
                        Logout
                    </button>
                </div>
            </div>

            {/* Spacer para el contenido principal */}
            <div style={{ width: '280px' }}></div>
        </div>
    );
};

export default Sidebar;