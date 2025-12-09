import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import loanService from "../../../services/loans.service";
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Button from 'react-bootstrap/Button';
import { Search } from "react-bootstrap-icons";
import Table from 'react-bootstrap/Table';
import DateRangeFilter from '../../common/DateRangeFilter';

const LoansPage = () => {
    const [loans, setLoans] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [searchType, setSearchType] = useState("name");
    const [statusFilter, setStatusFilter] = useState("all");
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const navigate = useNavigate();

    const init = () => {
        if (statusFilter === 'active') {
            loanService.getActiveLoans().then(response => {
                const data = filterByDateRange(response.data);
                setLoans(data);
            }).catch(error => console.log('Something went wrong', error));
            return;
        }

        if (statusFilter === 'inactive') {
            loanService.getInactiveLoans().then( (response) => {
                const data = filterByDateRange(response.data);
                setLoans(data);
            }).catch(error => console.log('Something went wrong', error));
            return;
        }

        loanService.getAllLoans().then(response => {
            console.log('Printing loans data', response.data);
            const data = filterByDateRange(response.data);
            setLoans(data);
        }).catch(error => {
            console.log('Something went wrong', error);
        });
    };

    const filterByDateRange = (loansData) => {
        if (!startDate && !endDate) {
        return loansData;
        }

        return loansData.filter(loan => {
        //normalize
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

   

    useEffect(() => {
        const timerId = setTimeout(() => {
            if (searchTerm) {
                loanService.searchLoans(searchType, searchTerm).then(response => {
                    let data = response.data;
                    // Apply client-side status filtering when searching
                    if (statusFilter !== 'all') {
                        const activeBool = statusFilter === 'active';
                        data = data.filter(l => l.active === activeBool);
                    }
                    data = filterByDateRange(data);
                    setLoans(data);
                }).catch(error => {
                    console.log('Something went wrong', error);
                    console.log("Error details:", error.response?.data);
                });
            } else {
                init();
            }
        }, 300);

        return () => clearTimeout(timerId);
    }, [searchTerm, searchType, statusFilter, startDate, endDate]);

    const clearDateFilter = () => {
        setStartDate(null);
        setEndDate(null);
    };

    return (
    <div className="container mt-5">
      <div className="card">
        <div className="card-header">
          <h1>Préstamos</h1>
        </div>
        <div className="card-body">
          {/* Filtros superiores */}
          <div className="row mb-3">
            <div className="col-md-4">
              <Form.Group>
                <Form.Label>Tipo de búsqueda</Form.Label>
                <Form.Select
                  value={searchType}
                  onChange={(e) => setSearchType(e.target.value)}
                >
                  <option value="name">Nombre</option>
                  <option value="rut">RUT</option>
                </Form.Select>
              </Form.Group>
            </div>

            <div className="col-md-4">
              <Form.Group>
                <Form.Label>Buscar</Form.Label>
                <InputGroup>
                  <InputGroup.Text>
                    <Search />
                  </InputGroup.Text>
                  <Form.Control
                    type="text"
                    placeholder={`Buscar por ${searchType === 'name' ? 'nombre' : 'RUT'}...`}
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </InputGroup>
              </Form.Group>
            </div>

            <div className="col-md-4">
              <Form.Group>
                <Form.Label>Estado</Form.Label>
                <Form.Select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                >
                  <option value="all">Todos</option>
                  <option value="active">Activos</option>
                  <option value="inactive">Inactivos</option>
                </Form.Select>
              </Form.Group>
            </div>
          </div>

          {/* Filtro de fechas */}
          <DateRangeFilter
            startDate={startDate}
            endDate={endDate}
            onStartDateChange={setStartDate}
            onEndDateChange={setEndDate}
            onClear={clearDateFilter}
            showActiveIndicator={true}
            label="Filtrar por rango de fechas"
          />

          {/* Tabla */}
          <Table striped bordered hover responsive>
            <thead>
              <tr>
                <th>ID Prestamo</th>
                <th>Nombre</th>
                <th>RUT</th>
                <th>Fecha pedido</th>
                <th>Fecha devolución</th>
                <th>Precio</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {loans.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center">
                    No se encontraron préstamos
                  </td>
                </tr>
              ) : (
                loans.map((loan) => (
                  <tr key={loan.loanId}>
                    <td>{loan.loanId}</td>
                    <td>
                      <Link to={`/loans/${loan.loanId}`}>
                        {loan.client?.name || 'Cargando...'}
                      </Link>
                    </td>
                    <td>{loan.client?.rut || 'Cargando...'}</td>
                    <td>{new Date(loan.loanStart).toLocaleDateString('es-ES')}</td>
                    <td>{new Date(loan.loanEnd).toLocaleDateString('es-ES')}</td>
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

          <Button variant="outline-dark" onClick={() => navigate('/loans/add')}>
            Nuevo Préstamo
          </Button>
        </div>
      </div>
    </div>
  );
};

export default LoansPage;