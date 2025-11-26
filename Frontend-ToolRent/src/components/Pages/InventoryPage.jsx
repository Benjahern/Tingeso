import React, {useEffect, useState} from 'react';
import Card from 'react-bootstrap/Card';
import { Link, useNavigate} from 'react-router-dom';
import toolService from "../../services/tool.service";
import { Button } from 'react-bootstrap';
import { Hammer } from "react-bootstrap-icons"
import httpClient from '../../http-common';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { useKeycloak } from '@react-keycloak/web';
const API_BASE = (httpClient.defaults.baseURL || '').replace(/\/api\/?$/, '');



const InventoryPage = () => {
    const [inventory, setInventory] = useState([]);
    const { keycloak } = useKeycloak();
    const isAdmin = keycloak?.hasRealmRole("ADMIN") || false;

    const navigate = useNavigate();
    
    const init = () => {
        toolService.getAll().then(response => {
            console.log('Printing inventory data', response.data);
            setInventory(response.data);
        }).catch(error => {
            console.log('Something went wrong', error);
        });
    };
    useEffect(() => {
        init();
    }, []);

    const handleEdit = (toolId) => {
        navigate(`/tools/add/${toolId}`);
    };

    const handleHistory = (toolId) => {
        navigate(`/tools/history/${toolId}`);
    }

    const handleTotalHistory = () => {
        navigate (`/history`)
    }

    return (
        <div className='container mt-4'>
            <Link to="/tools/add" disabled={!isAdmin}>
                <Button variant='outline-dark' className='mb-3' disabled={!isAdmin}>
                    <Hammer className='me-2' />
                    Añadir Herramienta
                </Button>
            </Link>
            <Button
                variant='outline-dark'
                className='mb-3'
                onClick={(e) =>{
                    e.preventDefault();
                    e.stopPropagation();
                    handleTotalHistory();
                }}
            >
                Kardex

            </Button>
            <div className="card mb-4">
                <Row xs={1} md={3} className="g-4">
                {inventory.map(tool => (
                    <Col key={tool.toolId} >
                        <Card className="h-100" style={{width: "fit-content"}}>
                            <div className="ratio ratio-1x1" >
                                <Card.Img
                                variant="top"
                                className="object-fit-cover"   // Bootstrap 5.3+
                                // style={{ objectFit: 'cover' }} // Fallback si no tienes la utilidad
                                src={tool.imagePath && tool.imagePath.startsWith('/uploads') ? API_BASE + tool.imagePath : tool.imagePath}
                                alt={tool.toolName}
                                />
                            </div>

                            <Card.Body>
                                <Card.Title>{tool.toolName}</Card.Title>
                                <Card.Text>
                                    <strong>Categoria:</strong> {tool.category}<br />
                                    <strong>Descripcion:</strong> {tool.description}<br />
                                    <strong>Precio por dia:</strong> {tool.dailyPrice}<br />
                                    <strong>Stock disponible:</strong> {tool.stock}<br />
                                    <strong>Valor repuesto:</strong> {tool.replacementValue}<br />
                                </Card.Text>
                                <Button variant="outline-dark" className='position-relative z-3' onClick={(e) => {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    handleEdit(tool.toolId);
                                }}>
                                    Editar
                                </Button>
                                <Button variant="outline-dark" className='ms-2 position-relative z-3' onClick={(e) => {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    handleHistory(tool.toolId);
                                }}>
                                    Historial
                                </Button>

                                <Link
                                    to={`/tools/${tool.toolId}`}
                                    className="stretched-link"
                                />
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
                </Row>
            </div>
        </div>
    );





};

export default InventoryPage;