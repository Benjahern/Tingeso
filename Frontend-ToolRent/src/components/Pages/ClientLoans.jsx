import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import clientService from '../../services/client.service';
import DateRangeFilter from '../common/DateRangeFilter';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import loansService from '../../services/loans.service';


const filterByDateRange = (loansData, startDate, endDate) => {
  if (!startDate && !endDate) {
    return loansData;
  }

  return loansData.filter((loan) => {
    if (!loan.loanStart) return false;

    // normalizar fecha del préstamo (solo día)
    const loanTimestamp = new Date(loan.loanStart).setHours(0, 0, 0, 0);

    if (startDate && endDate) {
      const start = new Date(startDate).setHours(0, 0, 0, 0);
      const end = new Date(endDate).setHours(23, 59, 59, 999);
      return loanTimestamp >= start && loanTimestamp <= end;
    } else if (startDate) {
      const start = new Date(startDate).setHours(0, 0, 0, 0);
      return loanTimestamp >= start;
    } else if (endDate) {
      const end = new Date(endDate).setHours(23, 59, 59, 999);
      return loanTimestamp <= end;
    }

    return true;
  });
};


const ClientLoans = () => {
    const { clientId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    const [client, setClient] = useState(null);
    const [loans, setLoans] = useState([]);
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const clientResponse = await clientService.getClient(clientId);
                setClient(clientResponse.data);

                const loansResponse = await loansService.getLoansByClientId(clientId);
                const data = Array.isArray(loansResponse.data) ? loansResponse.data : (loansResponse.data.content || loansResponse.data || []);
                setLoans(data);
            } catch (error) {
                console.error("Error fetching client or loans data:", error);
            }
        };

        fetchData();
    }, [clientId]);

    const handleClearDates = () => {
        setStartDate(null);
        setEndDate(null);
    };

    const handleLoanClick = (loanId) => {
        navigate(`/loans/${loanId}`);
    };

    const handleBackToClients = () => {
        navigate('/clients');
    };

    const filteredLoans = filterByDateRange(loans, startDate, endDate);
    
    return (
        <div className="container mt-4">
            <Card>
                <Card.Header>Préstamos del Cliente</Card.Header>
                <Button variant='secondary'
                    className="m-3"
                    onClick={handleBackToClients}>
                    Volver a Clientes
                    </Button>
                <Card.Body>
                    {client && (
                        <div className="mb-3">
                            <h5>{client.name}</h5>
                            <Table striped bordered hover>
                                <thead>
                                    <tr>
                                        <th>Rut</th>
                                        <th>Telefono</th>
                                        <th>Email</th>
                                        <th>Dirección</th>
                                        <th>Deuda Total</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>{client.rut}</td>
                                        <td>{client.phone}</td>
                                        <td>{client.mail}</td>
                                        <td>{client.address}</td>
                                        <td>
                                            {new Intl.NumberFormat('es-CL', {
                                                style: 'currency',
                                                currency: 'CLP',
                                                minimumFractionDigits: 0
                                            }).format(client.totalDebt || 0)}
                                        </td>
                                    </tr>
                                </tbody>
                            </Table>
                        </div>
                    )}
                    <div className="mb-3 d-flex justify-content-center">
                        <DateRangeFilter
                            startDate={startDate}
                            endDate={endDate}
                            onStartDateChange={setStartDate}
                            onEndDateChange={setEndDate}
                            onClear={handleClearDates}
                            label="Filtrar por rango de fechas"
                        />
                    </div>

                    <Table striped bordered hover responsive>
                        <thead>
                            <tr>
                                <th>ID Préstamo</th>
                                <th>Fecha Pedido</th>
                                <th>Fecha Devolución</th>
                                <th>Precio</th>
                                <th>Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredLoans.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="text-center">
                                        No se encontraron préstamos
                                    </td>
                                </tr>
                            ) : (
                                filteredLoans.map((loan) => (
                                    <tr key={loan.loanId}
                                        style={{ cursor: "pointer"}}
                                        onClick={() => handleLoanClick(loan.loanId)}>
                                        <td>{loan.loanId}</td>
                                        <td>
                                            {loan.loanStart ? new Date(loan.loanStart).toLocaleDateString() : "-"}
                                        </td>
                                        <td>
                                            {loan.loanEnd ? new Date(loan.loanEnd).toLocaleDateString() : "-"}
                                        </td>
                                        <td>
                                            {new Intl.NumberFormat('es-CL', {
                                                style: 'currency',
                                                currency: 'CLP',
                                                minimumFractionDigits: 0
                                            }).format(loan.price)}
                                        </td>
                                        <td>
                                            <span className={`badge ${loan.active ? 'bg-success' : 'bg-secondary'}`}>
                                                {loan.active ? 'Activo' : 'Inactivo'}
                                            </span>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>
        </div>
    )




};

export default ClientLoans;