import React, { useState, useEffect } from 'react';
import loanService from '../../services/loans.service';
import "../Style/ClientsFine.css"
import { Table } from 'react-bootstrap';

const ClientsWithFine = () => {
  const [clients, setClients] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const clientsPerPage = 5;

  useEffect(() => {
    fetchClientsWithFine();
  }, []);

  const fetchClientsWithFine = async () => {
    try {
      const response = await loanService.getClientsWithFine(0);
      setClients(response.data);
    } catch (err) {
      console.error('Error fetching clients with fine:', err);
    } 
  };

  const indexOfLastClient = currentPage * clientsPerPage;
  const indexOfFirstClient = indexOfLastClient - clientsPerPage;
  const currentClients = clients.slice(indexOfFirstClient, indexOfLastClient);
  const totalPages = Math.ceil(clients.length / clientsPerPage);

  const goToNextPage = () => {
    setCurrentPage((prev) => Math.min(prev + 1, totalPages));
  };

  const goToPreviousPage = () => {
    setCurrentPage((prev) => Math.max(prev - 1, 1));
  };

  const goToPage = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  return (
    <div className="w-100">
      <h2>Clientes con Atraso</h2>
      
      {clients.length === 0 ? (
        <p>No se encontraron clientes con multas.</p>
      ) : (
        <>
          <div className="results-info">
            Mostrando {indexOfFirstClient + 1} - {Math.min(indexOfLastClient, clients.length)} de {clients.length} clientes
          </div>

          <Table  >

            <thead>
              <tr>
                <th>Nombre</th>
                <th>Teléfono</th>
                <th>Estado</th>
                <th>Deuda Total</th>
              </tr>
            </thead>
            <tbody>
              {currentClients.map((client) => (
                <tr key={client.userId}>
                  <td>{client.name}</td>
                  <td>{client.phone}</td>
                  <td>
                    <span className={`status ${client.state?.toLowerCase()}`}>
                      {client.state || 'N/A'}
                    </span>
                  </td>
                  <td>${client.debt?.toFixed(2) || '0.00'}</td>
                  
                </tr>
              ))}
            </tbody>
          </Table>

          {totalPages > 1 && (
            <div className="pagination">
              <button 
                onClick={goToPreviousPage} 
                disabled={currentPage === 1}
                className="pagination-btn"
              >
                &#8592; Anterior
              </button>
              
              <div className="pagination-numbers">
                {[...Array(totalPages)].map((_, index) => (
                  <button
                    key={index + 1}
                    onClick={() => goToPage(index + 1)}
                    className={`pagination-number ${currentPage === index + 1 ? 'active' : ''}`}
                  >
                    {index + 1}
                  </button>
                ))}
              </div>
              
              <button 
                onClick={goToNextPage} 
                disabled={currentPage === totalPages}
                className="pagination-btn"
              >
                Siguiente &#8594;
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ClientsWithFine;
