import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import clientService from "../services/client.service";
import React from "react";
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import { PencilSquare, Trash, PersonPlusFill } from "react-bootstrap-icons";
import InputGroup from 'react-bootstrap/InputGroup';
import Form from 'react-bootstrap/Form';
import { Search } from "react-bootstrap-icons";


const ClientPage = () => {

    const [clients, setClients] = useState([]);

    const [searchTerm, setSearchTerm] = useState("");

    const [searchType, setSearchType] = useState("name"); 

    const navigate = useNavigate();

    const init = () => {
        clientService.getAllClients().then(response => {
            console.log('Printing clients data', response.data);
            setClients(response.data);
        }).catch(error => {
            console.log('Something went wrong', error);
        });
    };

    useEffect(() => {
        const timerId = setTimeout(() => {
            if (searchTerm) {
                clientService.searchClient(searchType, searchTerm).then(response => {
                    console.log('Search results:', response.data);
                    setClients(response.data);
                })
                .catch(error => {
                    console.log('Something went wrong during search', error);
                });
            } else {
                init();
            }
        }, 500);
        return () => clearTimeout(timerId);
    }, [searchTerm, searchType]);

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

    return (
        <div className="container mt-4">
            <Link to="/clients/add">
                <Button variant="outline-dark" className="mb-3">
                    <PersonPlusFill className="me-2" />
                    Añadir Cliente
                </Button>
            </Link>
            

                <div className="d-flex" style={{ width: '400px' }}>
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
                </div>
            

            <div className="w-100 d-flex justify-content-center">
                <Table striped bordered hover responsive >
                    <thead>
                        <tr>
                            <th style={{ width: '15%' }}>RUT</th>
                            <th style={{ width: '15%' }}>Nombre</th>
                            <th style={{ width: '20%' }}>Email</th>
                            <th style={{ width: '15%' }}>Teléfono</th>
                            <th style={{ width: '25%' }}>Dirección</th>
                            <th style={{ width: '10%' }}>Deuda</th>
                            <th style={{ width: '10%' }} className="text-center">Acciones</th>
                        </tr>
                    </thead>
                

                    <tbody>
                        {clients.map(client => (
                            <tr key={client.userId}>
                                <td>{client.rut}</td>
                                <td>{client.name}</td>
                                <td>{client.mail}</td>
                                <td>{client.phone}</td>
                                <td>{client.address}</td>
                                <td>{client.debt}</td>
                                <td className="text-center">
                                    <Button variant="outline-warning" onClick={() => handleEdit(client.userId)}>
                                        <PencilSquare />
                                    </Button>
                                    <Button variant="outline-danger" onClick={() => handleDelete(client.userId)}>
                                        <Trash />
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>


                </Table>
            </div>
                
        </div>
    );


};

export default ClientPage;
