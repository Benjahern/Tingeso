import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import clientService from "../../../services/client.service";
import React from "react";
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import { PencilSquare, Trash, PersonPlusFill } from "react-bootstrap-icons";
import InputGroup from 'react-bootstrap/InputGroup';
import Form from 'react-bootstrap/Form';
import { Search } from "react-bootstrap-icons";

// Page for the view of the clients

const ClientPage = () => {

    const [clients, setClients] = useState([]);

    const [searchTerm, setSearchTerm] = useState("");

    const [searchType, setSearchType] = useState("name");

    // Pagination state
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(1);
    const [displayedClients, setDisplayedClients] = useState([]);

    const navigate = useNavigate();

    const init = (page = currentPage, size = pageSize) => {
        clientService.getAllClients(page, size).then(response => {
            const data = response.data;

            // If backend returns a Spring Page-like object
            if (data && data.content && Array.isArray(data.content)) {
                setClients(data.content);
                setDisplayedClients(data.content);
                setTotalPages(data.totalPages || 1);
                setCurrentPage((data.number || 0) + 1); // Spring page.number is 0-based
                return;
            }

            // If backend returns an array (no server-side pagination), compute client-side pages
            if (Array.isArray(data)) {
                setClients(data);
                const pages = Math.max(1, Math.ceil(data.length / size));
                setTotalPages(pages);
                setCurrentPage(Math.min(page, pages));
                // slice the array for the requested page
                const start = (Math.min(page, pages) - 1) * size;
                const paged = data.slice(start, start + size);
                setDisplayedClients(paged);
                return;
            }

            // Fallback: if response is single client or other shape, normalize to array
            const arr = data ? (Array.isArray(data) ? data : [data]) : [];
            setClients(arr);
            setDisplayedClients(arr);
            setTotalPages(1);
            setCurrentPage(1);
        }).catch(error => {
            console.log('Something went wrong', error);
        });
    };

    useEffect(() => {
        const timerId = setTimeout(() => {
            // reset to first page when search term changes
            const pageToRequest = 1;
            if (searchTerm) {
                clientService.searchClient(searchType, searchTerm, pageToRequest, pageSize).then(response => {
                    const data = response.data;

                    if (data && data.content && Array.isArray(data.content)) {
                        setClients(data.content);
                        setDisplayedClients(data.content);
                        setTotalPages(data.totalPages || 1);
                        setCurrentPage((data.number || 0) + 1);
                        return;
                    }

                    if (Array.isArray(data)) {
                        setClients(data);
                        const pages = Math.max(1, Math.ceil(data.length / pageSize));
                        setTotalPages(pages);
                        setCurrentPage(1);
                        const start = 0;
                        setDisplayedClients(data.slice(start, start + pageSize));
                        return;
                    }

                    const arr = data ? (Array.isArray(data) ? data : [data]) : [];
                    setClients(arr);
                    setDisplayedClients(arr);
                    setTotalPages(1);
                    setCurrentPage(1);
                }).catch(error => {
                    console.log('Something went wrong during search', error);
                });
            } else {
                init(pageToRequest, pageSize);
            }

        }, 300);

        return () => clearTimeout(timerId);
    }, [searchTerm, searchType, pageSize]);

    // When currentPage changes (e.g., user clicks pagination), fetch the appropriate page
    useEffect(() => {
        // if searching, call search endpoint with page
        if (searchTerm) {
            clientService.searchClient(searchType, searchTerm, currentPage, pageSize).then(response => {
                const data = response.data;
                if (data && data.content && Array.isArray(data.content)) {
                    setClients(data.content);
                    setDisplayedClients(data.content);
                    setTotalPages(data.totalPages || 1);
                    setCurrentPage((data.number || 0) + 1);
                    return;
                }

                if (Array.isArray(data)) {
                    setClients(data);
                    const pages = Math.max(1, Math.ceil(data.length / pageSize));
                    setTotalPages(pages);
                    const start = (currentPage - 1) * pageSize;
                    setDisplayedClients(data.slice(start, start + pageSize));
                    return;
                }

                const arr = data ? (Array.isArray(data) ? data : [data]) : [];
                setClients(arr);
                setDisplayedClients(arr);
                setTotalPages(1);
            }).catch(error => console.log('Something went wrong during search', error));
        } else {
            init(currentPage, pageSize);
        }
    }, [currentPage]);

    const handleDelete = (id) => {
        console.log('Printing id', id);
        const confirmDelete = window.confirm('¿Esta seguro que desea eliminar este cliente?');
        if (confirmDelete) {
            clientService.deleteClient(id).then(response => {
                console.log('Client deleted successfully', response.data);
                init();
            }).catch(error => {
                console.log('Something went wrong', error);
            });
        }
    };

    const handleEdit = (id) => {
        console.log('Printing id', id);
        navigate(`/clients/add/${id}`);


    };

    const handleViewLoans = (client) => {
        navigate(`/clients/${client.clientId}/loans`, {
            state: {
                clientName: client.name,
                clientRut: client.rut,
            },
        });
    };

    return (
        <div className="container mt-4">
            <Link to="/clients/add">
                <Button variant="outline-dark" className="mb-3">
                    <PersonPlusFill className="me-2" />
                    Añadir Cliente
                </Button>
            </Link>


            {/*     HACER COMPONENTE DE ESTE DIV */}
            <div className="d-flex" style={{ width: '600px' }}>
                <Form.Select
                    value={searchType}
                    onChange={(e) => setSearchType(e.target.value)}
                    style={{ width: '120px' }}
                >
                    <option value="name">Nombre</option>
                    <option value="rut">RUT</option>
                    <option value="state">Estado</option>
                </Form.Select>

                <InputGroup className="ms-2">
                    <InputGroup.Text><Search /></InputGroup.Text>
                    <Form.Control
                        type="text"
                        placeholder="Buscar cliente..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </InputGroup>

                <Form.Select className="ms-2" value={pageSize} onChange={(e) => { setPageSize(Number(e.target.value)); setCurrentPage(1); }} style={{ width: '120px' }}>
                    <option value={5}>5 / pág</option>
                    <option value={10}>10 / pág</option>
                    <option value={20}>20 / pág</option>
                </Form.Select>
            </div>

            {/*     HASTA AQUI HACER COMPONENTE DE ESTE DIV */}
            <div className="w-100 d-flex justify-content-center">
                <Table striped bordered hover responsive >
                    <thead>
                        <tr>
                            <th>RUT</th>
                            <th>Nombre</th>
                            <th>Email</th>
                            <th>Teléfono</th>
                            <th>Dirección</th>
                            <th>Deuda</th>
                            <th>Estado</th>
                            <th className="text-center">Acciones</th>
                        </tr>
                    </thead>


                    <tbody>
                        {displayedClients.map(client => (
                            <tr key={client.clientId}>
                                <td>{client.rut}</td>
                                <td>
                                    <Button variant="link" onClick={() => handleViewLoans(client)}>
                                        {client.name}
                                    </Button>
                                </td>
                                <td>{client.mail}</td>
                                <td>{client.phone}</td>
                                <td>{client.address}</td>
                                <td>
                                    {new Intl.NumberFormat('es-CL', {
                                        style: 'currency',
                                        currency: 'CLP',
                                        minimumFractionDigits: 0
                                    }).format(client.debt)}
                                </td>
                                <td>
                                    <span className={`badge ${client.state === 'ACTIVO' ? 'bg-success' : client.state === 'RESTRINGIDO' ? 'bg-warning' : 'bg-secondary'}`}>
                                        {client.state}
                                    </span>
                                </td>
                                <td className="text-center">
                                    <Button variant="outline-warning" onClick={() => handleEdit(client.clientId)}>
                                        <PencilSquare />
                                    </Button>
                                    <Button variant="outline-danger" onClick={() => handleDelete(client.clientId)}>
                                        <Trash />
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>


                </Table>
            </div>

            {/* Pagination controls */}
            <div className="d-flex justify-content-center align-items-center mt-3">
                <Button variant="outline-dark" className="me-2" disabled={currentPage <= 1} onClick={() => setCurrentPage(1)}>Primera</Button>
                <Button variant="outline-dark" className="me-2" disabled={currentPage <= 1} onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}>Anterior</Button>
                <div>Pagina {currentPage} / {totalPages}</div>
                <Button variant="outline-dark" className="ms-2" disabled={currentPage >= totalPages} onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}>Siguiente</Button>
                <Button variant="outline-dark" className="ms-2" disabled={currentPage >= totalPages} onClick={() => setCurrentPage(totalPages)}>Última</Button>
            </div>

        </div>
    );


};

export default ClientPage;
