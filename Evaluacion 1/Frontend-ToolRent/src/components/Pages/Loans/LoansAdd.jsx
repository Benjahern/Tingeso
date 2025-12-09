import React, { use, useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import ClientSearchTypeahead from '../../common/ClientTypeaHead';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import Form from 'react-bootstrap/Form';
import Alert from 'react-bootstrap/Alert';
import loansService from '../../../services/loans.service';
import { PersonFill, XCircleFill } from 'react-bootstrap-icons';
import Badge from 'react-bootstrap/Badge';
import toolService from '../../../services/tool.service';
import DateRangeFilter from '../../common/DateRangeFilter';




const LoansAdd = () => {
    const [client, setClient] = useState(null);
    const minDate = new Date();
    const [store, setStore] = useState(null);
    const [startDate, setStartDate] = useState(new Date());
    const [endDate, setEndDate] = useState(new Date());
    const [price, setPrice] = useState(0);
    const [units, setUnits] = useState([]);
    const [active, setActive] = useState(true);
    const [selectedTools, setSelectedTools] = useState([]);

    

    const location = useLocation();
    const [selectedToolsIds, setSelectedToolsIds] = useState([]);
    const navigate = useNavigate();
    
    useEffect(() => {
        if (location.state?.selectedToolsIds) {
            const uniqueIds = [...new Set(location.state.selectedToolsIds)];
            
            console.log("Selected Tools IDs (original):", location.state.selectedToolsIds);
            console.log("Selected Tools IDs (únicos):", uniqueIds);
            
            setSelectedToolsIds(uniqueIds);
            loadToolsDetails(uniqueIds);
        }
    }, [location.state]);


    const handleClientSelect = (selectedClient) => {
        setClient(selectedClient);
    };

    const handleClearSelect = () => {
        setClient(null);
    };

    const loadToolsDetails = async (toolIds) => {
        try {
            const uniqueToolIds = [...new Set(toolIds)];
            console.log("Tool IDs recibidos:", toolIds);
            console.log("Tool IDs únicos:", uniqueToolIds);
            
            const toolsPromises = uniqueToolIds.map(id => toolService.get(id));
            const toolsResponses = await Promise.all(toolsPromises);
            const toolsData = toolsResponses.map(response => response.data);
            setSelectedTools(toolsData);
        } catch (error) {
            console.error("Error loading tools details:", error);
        }
    };


    const handleCreateLoan = () => {
        if(!client){
            alert("Por favor, selecciona un cliente antes de continuar.");
            return;
        }
        if (selectedToolsIds.length === 0){
            alert("Por favor, selecciona al menos una herramienta para el préstamo.");
            return;
        } 
        if (new Date(endDate) < new Date(startDate)){
            alert("La fecha de fin no puede ser anterior a la fecha de inicio.");
            return;
        }

        // Enviar fechas como 'YYYY-MM-DD' porque el backend usa LocalDate.parse
        const toIsoDate = (d) => new Date(d).toISOString().slice(0, 10);

        const loanData = {
            clientId: client.userId,
            storeId: 1, // Aquí deberías obtener el ID de la tienda seleccionada (worker)
            startDate: toIsoDate(startDate),
            endDate: toIsoDate(endDate),
            toolIds: selectedToolsIds,
        };

        console.log('Creating loan with payload:', loanData);

        try {
            loansService.createLoan(loanData).then(response => {
                console.log("Loan created successfully:", response.data);
                navigate('/loans'); // Redirigir a la página de préstamos después de crear el préstamo
            }).catch(error => {
                console.error("Error creating loan:", error);
                // Mostrar detalle del error si el backend devuelve un body
                if (error.response && error.response.data) {
                    console.error('Backend error body:', error.response.data);
                    alert('Hubo un error al crear el préstamo: ' + (error.response.data.error || JSON.stringify(error.response.data)));
                } else {
                    alert("Hubo un error al crear el préstamo. Por favor, inténtalo de nuevo.");
                }
            });
        } catch (error) {
            console.error("Unexpected error:", error);
            alert("Ocurrió un error inesperado. Por favor, inténtalo de nuevo.");
        }
    };

    const clearDateFilter = () => {
        setStartDate(null);
        setEndDate(null);
    };

    const calculatedMinEndDate = startDate ? new Date(new Date(startDate).getTime() + 86400000) : minDate;

    

    return (
        <div className='container mt-4'>
            <Card>
                <Card.Header> <h2>Añadir Préstamo</h2> </Card.Header>
                <Card.Body>
                    <div className='mb-4'>
                        <h5 className='mb-3'>Seleccionar Cliente</h5>
                        {!client ? (
                            <ClientSearchTypeahead 
                                onClientSelect={handleClientSelect}
                                selectedClient={client}
                                label="Buscar Cliente"
                                placeholder="Escribe el RUT o nombre del cliente..."
                            />
                        ) : (
                            <Card className='border-succes bg-light'>
                                <Card.Body>
                                    <div className="d-flex justify-content-between align-items-start">
                                        <div className='d-flex align-items-center'>
                                            <PersonFill size={40} className="me-3 text-primary" />
                                            <div>
                                                <h5 className="mb-1">{client.name}</h5>
                                                <p className="mb-0 text-muted">
                                                    <strong>RUT:</strong> {client.rut} 
                                                </p>
                                            </div>
                                        </div>
                                        <Button variant="outline-danger" 
                                            size='sm'
                                            onClick={handleClearSelect}
                                            >
                                                <XCircleFill className='me-1' />
                                            </Button>
                                    </div>

                                </Card.Body>
                            </Card>
                        )}

                    </div>

                    <hr />
                    <div className='mb-4'>
                        <h5 className='mb-3'>Herramientas seleccionadas</h5>
                        {selectedToolsIds.length > 0 ? (
                            <Card>
                                <Card.Body>
                                    <div className='mb-2'>
                                        <Badge bg="primary" pill>
                                            {selectedToolsIds.length} herramienta(s) seleccionada(s)
                                        </Badge>
                                    </div>
                                    <div className="list-group">
                                        {selectedTools.map((tool) => (
                                            <div 
                                                key={tool.toolId}
                                                className="list-group-item d-flex justify-content-between align-items-center"
                                            >
                                                <div>
                                                    <strong>{tool.toolName}</strong>
                                                    <div className='text-muted small'>
                                                        Categoria: {tool.category} |
                                                        Precio/dia: {tool.dailyPrice}
                                                    </div>
                                                </div>                                                    
                                    
                                            </div>
                                        ))}

                                    </div>
                                </Card.Body>
                            </Card>
                        ) : (
                            <Alert variant='info'>
                                No hay herramientas seleccionadas para el préstamo.
                            </Alert>
                        )}
                    </div>
                    <hr />

                    <div className='mb-4'>
                        <h5 className='mb-3'>Fechas del Préstamo</h5>
                        <div className='row'>
                            
                            <DateRangeFilter
                                startDate={startDate}
                                endDate={endDate}
                                minDate={minDate}
                                minEndDate={calculatedMinEndDate}
                                onStartDateChange={setStartDate}
                                onEndDateChange={setEndDate}
                                onClear={clearDateFilter}
                                showActiveIndicator={true}
                                label="Filtrar por rango de fechas"
                            />

                        </div>
                    </div>

                    <Button variant='outline-secondary' onClick={() => navigate("/loans")}>
                        Volver
                    </Button>

                    <Button type='submit' variant='outline-dark'
                        onClick={handleCreateLoan}
                        disabled={!client || selectedToolsIds.length === 0} 
                        >Crear Préstamo
                    </Button>
                            
                    
                    
                    

                </Card.Body>
            </Card>
        
        </div>
    );
};

export default LoansAdd;



