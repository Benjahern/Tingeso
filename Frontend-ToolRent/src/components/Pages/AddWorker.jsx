import React, { useState, useEffect} from 'react';
import { Container, Card, Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import workerService from '../../services/worker.service';
import roleService from '../../services/role.service';

const AddWorker = () => {
    // the struct data for the worker-user
    const [formData, setFormData] = useState({
        name: '',
        mail: '',
        password: '',
        storeId: 1
    });

    const [selectedRoles, setSelectedRoles] = useState([]);
    const [roles, setRoles] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadRoles();
    }, []);

    //for the assignation of the roles
    const loadRoles = async () => {
        try{
            const response = await roleService.getAllRoles();
            setRoles(response.data);

        } catch (error){
            console.error('Error loading roles:', error);
        }
    };
    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };


    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!selectedRoles || selectedRoles.length === 0) {
            alert('Por favor selecciona al menos un rol.');
            return;
        }

        try {
            const dataToSend = {
                ...formData,
                roleIds: selectedRoles.map(id => Number(id))
            };
            await workerService.createWorker(dataToSend);
            alert('Worker added successfully!');
            navigate('/employees');
        } catch (error) {
            console.error('Error adding worker:', error);
            alert('Failed to add worker. Please try again.');
        }
    };

    return (
        <Container className="mt-4">
            <Card>
                <Card.Header>
                    <h3>Añadir a nuevo trabajador</h3>
                </Card.Header>
                <Card.Body>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3" >
                            <Form.Label>Nombre y apellido</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                placeholder="Ingrese nombre y apellido"
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3" >
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                name="mail"
                                value={formData.mail}
                                onChange={handleChange}
                                placeholder="correo@ejemplo.com"
                                required
                            />
                            <Form.Text className='text-muted'>
                                Este email se usará para iniciar sesión en el sistema
                            </Form.Text>
                        </Form.Group>
                        <Form.Group className='mb-3'>
                            <Form.Label>Contraseña</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Ingrese una contraseña"
                                minLength={8}
                                required
                            />
                            <Form.Text className='text-muted'>
                                La contraseña debe tener al menos 8 caracteres.
                            </Form.Text>
                        </Form.Group>
                        <Form.Group className="mb-3" >
                            <Form.Label>Roles</Form.Label>
                                {roles.map(role => (
                                    <Form.Check
                                        key={role.rolId}
                                        type="checkbox"
                                        id={`role-${role.rolId}`}
                                        label={role.rolName}
                                        value={role.rolId}
                                        checked={selectedRoles.includes(role.rolId.toString())}
                                        onChange={(e) => {
                                        if (e.target.checked) {
                                            setSelectedRoles([...selectedRoles, role.rolId.toString()]);
                                        } else {
                                            setSelectedRoles(selectedRoles.filter(id => id !== role.rolId.toString()));
                                        }
                                        }}
                                    />
                                    ))}
                        </Form.Group>

                        <div className='d-flex gap-2'>
                            <Button variant="outline-dark" type="submit">
                                Guardar
                            </Button>
                            <Button variant="secondary" onClick={() => navigate('/employees')}>
                                Cancelar
                            </Button>
                        </div>

                    </Form>
                </Card.Body>
            </Card>
        </Container>
    )


};

export default AddWorker;