import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import Badge from 'react-bootstrap/Badge';
import kardexService from '../../services/kardex.service';
import DateRangeFilter from '../common/DateRangeFilter';
import KardexExcel from '../common/KardexExcel';
import { FileEarmarkSpreadsheet, BoxSeam, Funnel } from 'react-bootstrap-icons';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

const KardexList = () => {
    const [allKardex, setAllKardex] = useState([]);
    const [filteredKardex, setFilteredKardex] = useState([]);
    const [loading, setLoading] = useState(true);
    
    const navigate = useNavigate();
    const { exportToExcel } = KardexExcel();

    // Filtros
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [movementFilter, setMovementFilter] = useState('');
    const [workerFilter, setWorkerFilter] = useState('');
    const [toolFilter, setToolFilter] = useState('');

    // Listas para filtros
    const [movements, setMovements] = useState([]);
    const [workers, setWorkers] = useState([]);
    const [tools, setTools] = useState([]);

    const clearDateFilter = () => {
        setStartDate(null);
        setEndDate(null);
    };

    const clearAllFilters = () => {
        setStartDate(null);
        setEndDate(null);
        setMovementFilter('');
        setWorkerFilter('');
        setToolFilter('');
    };

    // Cargar todos los kardex
    const init = () => {
        setLoading(true);
        kardexService.getAllKardex().then(response => {
            console.log('Printing all kardex data', response.data);
            setAllKardex(response.data);
            setFilteredKardex(response.data);

            // Extraer valores únicos para los filtros
            const uniqueMovements = [...new Set(response.data.map(k => k.movement))].filter(Boolean);
            const uniqueWorkers = [...new Set(response.data.map(k => k.worker?.name))].filter(Boolean);
            const uniqueTools = [...new Set(response.data.map(k => k.tool?.name))].filter(Boolean);

            setMovements(uniqueMovements.sort());
            setWorkers(uniqueWorkers.sort());
            setTools(uniqueTools.sort());
            
            setLoading(false);
        }).catch(error => {
            console.log('Error loading kardex', error);
            setLoading(false);
        });
    };

    // Aplicar filtros
    useEffect(() => {
        let filtered = [...allKardex];

        // Filtro por fechas
        if (startDate && endDate) {
            filtered = filtered.filter(record => {
                const recordDate = new Date(record.date);
                const recordDateOnly = new Date(recordDate.getFullYear(), recordDate.getMonth(), recordDate.getDate());
                const startDateOnly = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
                const endDateOnly = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
                
                return recordDateOnly >= startDateOnly && recordDateOnly <= endDateOnly;
            });
        }

        // Filtro por tipo de movimiento
        if (movementFilter) {
            filtered = filtered.filter(record => record.movement === movementFilter);
        }

        // Filtro por trabajador
        if (workerFilter) {
            filtered = filtered.filter(record => record.worker?.name === workerFilter);
        }

        

        setFilteredKardex(filtered);
    }, [startDate, endDate, movementFilter, workerFilter, toolFilter, allKardex]);

    useEffect(() => {
        init();
    }, []);

    // Exportar a Excel
    const handleExport = () => {
        const dataToExport = filteredKardex.map(record => ({
            kardexId: record.kardexId,
            toolName: record.unit?.tool?.toolName || 'N/A',
            toolId: record.unit?.tool?.toolId|| 'N/A',
            movement: record.movement,
            date: new Date(record.date).toLocaleDateString(),
            unitId: record.unit?.unitId || 'N/A',
            stockBalance: record.stockBalance,
            workerName: record.worker?.name || 'N/A',
            storeName: record.store?.name || 'N/A',
            comment: record.comment || '-'
        }));

        const headers = {
            kardexId: 'ID Movimiento',
            toolName: 'Herramienta',
            toolId: 'ID Herramienta',
            movement: 'Tipo de Movimiento',
            date: 'Fecha',
            unitId: 'Unidad',
            stockBalance: 'Stock',
            workerName: 'Trabajador',
            comment: 'Comentario'
        };

        const columnWidths = [12, 25, 12, 20, 12, 10, 10, 20, 20, 30];

        exportToExcel(
            dataToExport,
            'Kardex_General',
            'Kardex',
            columnWidths,
            headers
        );
    };

    // Obtener badge de color según tipo de movimiento
    const getMovementBadge = (movement) => {
        const badges = {
            'ENTRADA': 'success',
            'SALIDA': 'danger',
            'PRESTAMO': 'primary',
            'DEVOLUCION': 'info',
            'AJUSTE': 'warning'
        };
        return <Badge bg={badges[movement] || 'secondary'}>{movement}</Badge>;
    };

    if (loading) {
        return (
            <div className="container mt-4 text-center">
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Cargando...</span>
                </div>
            </div>
        );
    }

    return (
        <div className='container mt-4'>
            <Card className="mb-4">
                <Card.Header className="d-flex justify-content-between align-items-center">
                    <div>
                        <h2 className="mb-0">
                            <BoxSeam className="me-2" />
                            Kardex General
                        </h2>
                    </div>
                    <Button 
                        variant="success" 
                        onClick={handleExport}
                        disabled={filteredKardex.length === 0}
                    >
                        <FileEarmarkSpreadsheet className="me-2" />
                        Exportar a Excel
                    </Button>
                </Card.Header>

                <Card.Body>
                    {/* Filtros */}
                    <div className="mb-4">
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <h5 className="mb-0">
                                <Funnel className="me-2" />
                                Filtros
                            </h5>
                            {(startDate || endDate || movementFilter || workerFilter || toolFilter) && (
                                <Button 
                                    variant="outline-secondary" 
                                    size="sm"
                                    onClick={clearAllFilters}
                                >
                                    Limpiar todos los filtros
                                </Button>
                            )}
                        </div>

                        {/* Filtro de fechas */}
                        <div className='b-3 d-flex justify-content-center'>
                            <DateRangeFilter
                                startDate={startDate}
                                endDate={endDate}
                                onStartDateChange={setStartDate}  
                                onEndDateChange={setEndDate}      
                                onClear={clearDateFilter}         
                            />
                        </div>

                        {/* Otros filtros */}
                        <Row>
                            <Col md={4}>
                                <Form.Group>
                                    <Form.Label>Tipo de Movimiento</Form.Label>
                                    <Form.Select 
                                        value={movementFilter}
                                        onChange={(e) => setMovementFilter(e.target.value)}
                                    >
                                        <option value="">Todos</option>
                                        {movements.map(movement => (
                                            <option key={movement} value={movement}>
                                                {movement}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>

                            <Col md={4}>
                                <Form.Group>
                                    <Form.Label>Trabajador</Form.Label>
                                    <Form.Select 
                                        value={workerFilter}
                                        onChange={(e) => setWorkerFilter(e.target.value)}
                                    >
                                        <option value="">Todos</option>
                                        {workers.map(worker => (
                                            <option key={worker} value={worker}>
                                                {worker}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>

                        
                        </Row>
                    </div>

                    {/* Contador de resultados */}
                    <div className="mb-3 text-muted">
                        <small>
                            {(startDate || endDate || movementFilter || workerFilter || toolFilter) ? (
                                <>Mostrando <strong>{filteredKardex.length}</strong> de <strong>{allKardex.length}</strong> registros</>
                            ) : (
                                <>Mostrando todos los registros (<strong>{allKardex.length}</strong>)</>
                            )}
                        </small>
                    </div>

                    {/* Tabla */}
                    <div className="table-responsive">
                        <Table striped bordered hover>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Herramienta</th>
                                    <th>Tipo</th>
                                    <th>Fecha</th>
                                    <th>Unidad</th>
                                    <th>Stock</th>
                                    <th>Trabajador</th>
                                    <th>Comentario</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredKardex.length > 0 ? (
                                    filteredKardex.map(record => (
                                        <tr key={record.kardexId}>
                                            <td>{record.kardexId}</td>
                                            <td>
                                                <strong>{record.unit?.tool?.toolName || 'N/A'}</strong>
                                                <br />
                                                <small className="text-muted">ID: {record.unit?.tool?.toolId || 'N/A'}</small>
                                            </td>
                                            <td>{getMovementBadge(record.movement)}</td>
                                            <td>{new Date(record.date).toLocaleDateString()}</td>
                                            <td>{record.unit?.unitId || 'N/A'}</td>
                                            <td>
                                                <Badge bg="secondary">{record.stockBalance}</Badge>
                                            </td>
                                            <td>{record.worker?.name || 'N/A'}</td>
                                            <td>
                                                <small>{record.comment || '-'}</small>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="9" className="text-center text-muted py-4">
                                            {allKardex.length === 0 
                                                ? 'No hay registros de kardex disponibles' 
                                                : 'No hay registros que coincidan con los filtros seleccionados'}
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </Table>
                    </div>
                </Card.Body>
            </Card>
        </div>
    );
}

export default KardexList;
