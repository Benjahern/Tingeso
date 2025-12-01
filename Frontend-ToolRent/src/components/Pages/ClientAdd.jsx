import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import clientService from "../../services/client.service";
import React from "react";
import Form from 'react-bootstrap/Form';
import { InputGroup } from "react-bootstrap";

const ClientAdd = () => {
    const [rut, setRut] = useState("");
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [mail, setMail] = useState("");
    const [address, setAddress] = useState("");
    const [state, setState] = useState("ACTIVO");
    const [debt, setDebt] = useState(0);
    const [titleClientForm, setTitleClientForm] = useState("Añadir nuevo cliente");
    
    const [originalClient, setOriginalClient] = useState(null);

    const { id } = useParams();
    const navigate = useNavigate();

    const saveClient = (e) => {
        e.preventDefault();
        const client = {rut, name, phone, mail, address, state, debt };
        
                
                
        
        if (id) {
            // actualizar
            const confirmUpdate = window.confirm('¿Esta seguro que desea actualizar este cliente?');
            if (!confirmUpdate) {
                return;
            }

            clientService.updateClient(id, client).then(response => {
                console.log('Client data updated successfully', response.data);
                navigate('/clients');
            }).catch(error => {
                console.log('Something went wrong', error);
            });
        } else {
            // crear
            client.state = "ACTIVO"; // nuevo cliente siempre activo
            client.debt = 0; // nuevo cliente sin deuda
            clientService.createClient(client).then(response => {
                console.log('Client added successfully', response.data);
                navigate('/clients');
            }).catch(error => {
                console.log('Something went wrong', error);
            });
        }
    };

    useEffect(() => {
        if (id) {
            // editar
            setTitleClientForm("Editar cliente");
            console.log('El id del cliente es: ' + id);
            clientService.getClient(id).then(response => {
                setRut(response.data.rut);
                setName(response.data.name);
                setPhone(response.data.phone);
                setMail(response.data.mail);
                setAddress(response.data.address);
                setState(response.data.state.toUpperCase());
                setDebt(response.data.debt);
                setOriginalClient(response.data); 
            }).catch(error => {
                console.log('Something went wrong', error);
            });
        }else {
            // crear
            setTitleClientForm("Añadir nuevo cliente")
            setState("ACTIVO");
            setDebt(0);
        }
    }, [id]);


       const handleStateChange = (e) => {
            const newState = e.target.value;
            if (id && originalClient && newState !== originalClient.state) {
                const confirmChange = window.confirm('¿Está seguro de que desea cambiar el estado de este cliente?');
                if (confirmChange) {
                    setState(newState);
                }
            } else {
                setState(newState);
            }
        };
    

    const handleDebtChange = (e) => {
        const value = e.target.value;
        
        setDebt(value);
    };

    return (
        <Form onSubmit={saveClient}>
            <h3>{titleClientForm}</h3>
            <Form.Group className="mb-3" controlId="formRut">
                <Form.Label>RUT</Form.Label>
                <Form.Control 
                    type="text" 
                    placeholder="12345678-9"
                    value={rut}
                    onChange={(e) => setRut(e.target.value)}
                    required
                    disabled={!!id} // deshabilitar si es edición
                />
                {id && <Form.Text className="text-muted">El RUT no se puede modificar.</Form.Text>}
            </Form.Group>

            <Form.Group className="mb-3" controlId="formName">
                <Form.Label>Nombre</Form.Label>
                <Form.Control 
                    type="text" 
                    placeholder="Ingrese nombre"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formPhone">
                <Form.Label>Teléfono</Form.Label>
                <Form.Control 
                    type="text" 
                    placeholder="Ingrese teléfono"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formMail">
                <Form.Label>Correo Electrónico</Form.Label>
                <Form.Control 
                    type="email" 
                    placeholder="Ingrese correo electrónico"
                    value={mail}
                    onChange={(e) => setMail(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formAddress">
                <Form.Label>Dirección</Form.Label>
                <Form.Control 
                    type="text" 
                    placeholder="Ingrese dirección"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    required
                />
            </Form.Group>
            {id && (
                <>
                <Form.Group className="mb-3" controlId="formState">
                    <Form.Label>Estado</Form.Label>
                    <Form.Select value={state} onChange={handleStateChange}>
                        <option value="ACTIVO">ACTIVO</option>
                        <option value="RESTRINGIDO">RESTRINGIDO</option>
                    </Form.Select>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formDebt">
                    <Form.Label>Deuda</Form.Label>
                    <InputGroup>
                        <InputGroup.Text>$</InputGroup.Text>
                        <Form.Control type="number" value={debt} onChange={handleDebtChange} placeholder="0"/>
                    </InputGroup>
                </Form.Group>
                </>
            )}

            <button type="submit" className="btn btn-primary">Guardar</button>
            <Link to="/clients" className="btn btn-secondary" style={{ marginLeft: "10px" }}>Cancelar</Link>

        </Form>
    );
};


            





export default ClientAdd;
