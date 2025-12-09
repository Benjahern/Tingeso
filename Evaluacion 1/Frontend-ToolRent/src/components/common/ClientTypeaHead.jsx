import React, { useState, useEffect } from 'react';
import Form from 'react-bootstrap/Form';
import { Typeahead } from 'react-bootstrap-typeahead';
import { PersonFill } from 'react-bootstrap-icons';
import clientService from '../../services/client.service';
import 'react-bootstrap-typeahead/css/Typeahead.css';

const ClientSearchTypeahead = ({ onClientSelect, selectedClient, label, placeholder }) => {
    const [clientOptions, setClientOptions] = useState([]);
    const [isLoadingClients, setIsLoadingClients] = useState(false);
    const [selection, setSelection] = useState([]);

    useEffect(() => {
        loadClients();
    }, []);

    useEffect(() => {
        if (selectedClient) {
            setSelection([selectedClient]);
        } else {
            setSelection([]);
        }
    }, [selectedClient]);

    const loadClients = () => {
        setIsLoadingClients(true);
        clientService.getAllClients()
            .then(response => {
                setClientOptions(response.data);
                setIsLoadingClients(false);
            })
            .catch(error => {
                console.log('Error al cargar clientes', error);
                setIsLoadingClients(false);
            });
    };

    const handleChange = (selected) => {
        setSelection(selected);
        if (selected.length > 0) {
            onClientSelect(selected[0]);
        } else {
            onClientSelect(null);
        }
    };

    return (
        <Form.Group>
            {label && <Form.Label>{label}</Form.Label>}
            <Typeahead
                id="client-search-typeahead"
                labelKey={(option) => `${option.name} - ${option.rut}`}
                options={clientOptions}
                placeholder={placeholder || "Escribe el RUT o nombre del cliente..."}
                onChange={handleChange}
                selected={selection}
                isLoading={isLoadingClients}
                emptyLabel="No se encontraron clientes"
                filterBy={['name', 'rut']}  // ← SOLO nombre y RUT
                renderMenuItemChildren={(option) => (
                    <div className="d-flex align-items-center py-1">
                        <PersonFill size={20} className="me-2 text-primary" />
                        <div>
                            <div><strong>{option.name}</strong></div>
                            <small className="text-muted">
                                RUT: {option.rut}
                            </small>
                        </div>
                    </div>
                )}
            />
            <Form.Text className="text-muted">
                Escribe para buscar por nombre o RUT
            </Form.Text>
        </Form.Group>
    );
};

export default ClientSearchTypeahead;
