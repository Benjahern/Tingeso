import React, { useState, useEffect } from 'react';
import loanService from '../../services/loans.service';
import Table from 'react-bootstrap/Table';
import { Button } from 'react-bootstrap';

const ActiveLoans = () => {
  const [activeLoans, setActiveLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const loansPerPage = 5;

  useEffect(() => {
    fetchActiveLoans();
  }, []);

  const fetchActiveLoans = async () => {
    try {
      setLoading(true);
      const response = await loanService.getActiveLoans();
      setActiveLoans(response.data);
      setError(null);
    } catch (err) {
      setError('Error al cargar los préstamos activos');
      console.error('Error fetching active loans:', err);
    } finally {
      setLoading(false);
    }
  };
  
  const indexOfLastLoan = currentPage * loansPerPage;
  const indexOfFirstLoan = indexOfLastLoan - loansPerPage;
  const currentLoans = activeLoans.slice(indexOfFirstLoan, indexOfLastLoan);

  const totalPages = Math.ceil(activeLoans.length / loansPerPage);


  const handlePrevPage = () => {
    setCurrentPage(prev => (prev > 1 ? prev - 1 : prev));
  };

  const handleNextPage = () => {
    setCurrentPage(prev => (prev < totalPages ? prev + 1 : prev));
  };


  if (loading) {
    return <div className="loading">Cargando préstamos...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="active-loans-container">
      <h2>Préstamos Activos</h2>
      
      {activeLoans.length === 0 ? (
        <p>No hay préstamos activos en este momento.</p>
      ) : (
        <>
          <Table className="loans-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre del Cliente</th>
              <th>Valor</th>
              <th>Multa</th>
            </tr>
          </thead>
          <tbody>
            {currentLoans.map((loan) => (
              <tr key={loan.loanId}>
                <td>{loan.loanId}</td>
                <td>{loan.client?.name || 'N/A'}</td>
                <td>${loan.price?.toFixed(2) || '0.00'}</td>
                <td>${loan.fine || '0'}</td>
              </tr>
            ))}
          </tbody>
          </Table>

          <div className='pagination-controls'>
          <Button
            variant='outline-dark'
            onClick={handlePrevPage}
            disabled={currentPage===1}
          >
            Anterior
          </Button>
          <span>
            <Button
              variant='outline-dark'
              onClick={handleNextPage}
              disabled={currentPage=== totalPages}
            >
              Siguiente
            </Button>
          </span>
          </div>
        </>
      )}
    </div>
  );
};

export default ActiveLoans;
