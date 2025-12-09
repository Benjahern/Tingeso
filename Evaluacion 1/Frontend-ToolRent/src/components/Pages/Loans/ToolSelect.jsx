import React, {useEffect, useState} from 'react';
import Card from 'react-bootstrap/Card';
import { Link, useNavigate} from 'react-router-dom';
import toolService from '../../../services/tool.service';
import { Button } from 'react-bootstrap';
import httpClient from '../../../http-common';
import Row from 'react-bootstrap/Row';
import { CheckCircleFill } from 'react-bootstrap-icons';
import Badge from 'react-bootstrap/Badge';

import Col from 'react-bootstrap/Col';
const API_BASE = (httpClient.defaults.baseURL || '').replace(/\/api\/?$/, '');


const ToolSelect = () => {
    const [inventory, setInventory] = useState([]);
    const [selectedToolsIds, setSelectedToolsIds] = useState([]);
    const navigate = useNavigate();
    

    useEffect(() => {
        toolService.getAll().then(response => {
            console.log('Printing inventory data', response.data);
            setInventory(response.data);
        }).catch(error => {
            console.log('Something went wrong', error);
        });
    }, []);

    const handleToolSelect = (tool) => {
        if (tool.stock > 0) {
            setSelectedToolsIds(prevSelected => {
                if (prevSelected.includes(tool.toolId)) {
                    // Si ya está seleccionado, lo deseleccionamos
                    return prevSelected.filter(id => id !== tool.toolId);
                }
                return [...prevSelected, tool.toolId];
            });
        }
    }

    const isSelected = (toolId) => {
        return selectedToolsIds.includes(toolId);
    }

    const handleAdvance = () => {
        if (selectedToolsIds.length === 0) {
            alert('Por favor, seleccione al menos una herramienta para continuar.');
            return;
        }
        navigate("create", { state: { selectedToolsIds: selectedToolsIds } });
    }

    return (
        <div className='container mt-4'>
            <div className="card mb-4">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h1 className="mb-0">Seleccionar Herramientas</h1>
                    <Badge bg="dark" pill style={{ fontSize: '1rem' }}>
                        {selectedToolsIds.length} seleccionada{selectedToolsIds.length !== 1 ? 's' : ''}
                    </Badge>
                </div>
            </div>
            <div className="card mb-4">
                <Row xs={1} md={3} className="g-4">
                {inventory.map(tool => (
                    <Col key={tool.toolId} >
                        <Card className={`h-100 ${isSelected(tool.toolId) ? 'selected' : ''} ${tool.stock === 0 ? 'no-stock' : ''}`} 
                            onClick={() => handleToolSelect(tool)}
                            style={{ cursor: tool.stock > 0 ? 'pointer' : 'not-allowed', position: 'relative' }}
                        >
                            {isSelected(tool.toolId) && (
                                <div className="selected-overlay">
                                    <CheckCircleFill size={40} color='#28a745' />
                                </div>
                            )}
                            {tool.stock === 0 && (
                            <div className="no-stock-badge">
                                <Badge bg="danger">Sin Stock</Badge>
                            </div>
                            )}
                            <div className="ratio ratio-1x1">
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
                                
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
                </Row>

                <div className="mt-4 d-flex gap-2">
                    <Button
                        variant="success"
                        onClick={handleAdvance}
                        disabled={selectedToolsIds.length === 0}
                    >
                        Avanzar ({selectedToolsIds.length})
                    </Button>
                    <Button
                        variant='outline-secundary'
                        onClick={() => navigate("/loans")}
                        >
                        Cancelar
                        </Button>
                    
                </div>






            </div>

        </div>
    );





};

export default ToolSelect;