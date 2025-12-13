import unitService from "../../../services/unit.service";
import React, { useEffect, useState } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import { Button, Overlay } from 'react-bootstrap';
import { Hammer, XCircle } from "react-bootstrap-icons"
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import { Search } from "react-bootstrap-icons";
import Table from 'react-bootstrap/Table';
import { useKeycloak } from "@react-keycloak/web";
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';


const UnitPage = () => {

    const [unit, setUnit] = useState([]);

    const [searchTerm, setSearchTerm] = useState("");

    const [searchType, setSearchType] = useState("toolName");

    const navigate = useNavigate();

    const { keycloak} = useKeycloak();

    const isAdmin = keycloak.hasRealmRole("ADMIN");
    
    const {toolId} = useParams();

    const init = () => {
        unitService.getUnitsByToolId(toolId).then(response => {
            console.log('Printing units data', response.data);
            setUnit(response.data);
        }).catch(error => {
            console.log('Something went wrong', error);
        });
    };

    useEffect(() => {
        const timerId = setTimeout(() => {
            if (searchTerm) {
                unitService.searchUnits(searchType, searchTerm).then(response => {
                    console.log('Search results:', response.data);
                }).catch(error => {
                    console.log('Something went wrong during search', error);
                });
            } else {
                if(toolId){
                    init();
                }
            }

        }, 500);

        return () => clearTimeout(timerId);
    }, [searchTerm, searchType, toolId]);

    const handleSendToMaintenance = async (unitId) => {
        if (!isAdmin) {
            alert('Solo los administradores pueden dar de baja unidades');
            return;
      }
        const confirmSend = window.confirm("¿Está seguro de que desea enviar esta unidad a mantenimiento?");
        if (confirmSend) {
            try{
                await unitService.updateUnitStatus(unitId, { status: "En Mantenimiento", condition: "En mantenimiento" });
                init();
            }catch(error){
                console.log('Error sending unit to maintenance', error);
            }
        }
    };

    const handleDecomision = async (unitId, currentCondition) => {
        const confirmDecomision = window.confirm("¿Está seguro de que desea dar de baja esta unidad?");
        if (confirmDecomision) {
            try{
                await unitService.decommisionUnit(unitId, {
                    condition: currentCondition,
                    comment: "Dado de baja por administrador",    
                });
                init();
            }catch(error){
                console.log('Error decommissioning unit', error);
            }
        }
    };


    const handleEdit = (id) => {
        navigate(`/units/edit/${id}`, { state: { toolId } });
    };

    return (
        <div className="container mt-4">
            <Link to={`/tools/${toolId}/units/add`} state={{ toolId}}>
                <Button variant='outline-dark' className='mb-3'>
                    <Hammer className='me-2' />
                    Añadir Unidad
                </Button>
            </Link>

            <div className="w-100 d-flex justify-content-center">
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            <th>ID Unidad</th>
                            <th>Estado</th>
                            <th>Condición</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {unit.map(unit => (
                            <tr key={unit.unitId}>
                                <td>{unit.unitId}</td>
                                <td>
                                    <span className={`badge ${
                                    unit.status === 'Disponible' ? 'bg-success' :
                                    unit.status === 'Prestado' ? 'bg-warning' :
                                    unit.status === 'Dañado' ? 'bg-danger' :
                                    unit.status === 'En Mantenimiento' ? 'bg-info' :
                                    unit.status === 'Dado de Baja' ? 'bg-secondary' : 'bg-primary'
                                    }`}>
                                        {unit.status}
                                    </span>
                                </td>
                                <td>{unit.condition}</td>
                                <td>
                                    <Button variant="outline-primary" 
                                        onClick={() => handleEdit(unit.unitId)}
                                        className="me-2"
                                        disabled={unit.status === "Dado de Baja"}
                                    >Editar</Button>

                                    {unit.status === "Dañado" && (
                                        <>
                                            <Button variant="warning"
                                            size="sm"
                                            onClick={() => handleSendToMaintenance(unit.unitId, unit.condition)}
                                            className="me-2"
                                            title="Enviar a Mantenimiento"
                                            >
                                                <Hammer /> Mantenimiento
                                            </Button>
                                            <OverlayTrigger placement="top" overlay={<Tooltip> {isAdmin ? "Dar de Baja" : "Solo los administradores"}</Tooltip>}>
                                                <span className="d-inline-block">
                                                    <Button 
                                                    variant="danger" 
                                                    size="sm" 
                                                    onClick={() => handleDecomision(unit.unitId, unit.condition)}
                                                    disabled={!isAdmin}
                                                    >
                                                    <XCircle className="me-1" /> Dar de Baja
                                                    </Button>
                                                </span>
                                            </OverlayTrigger>
                                            
                                        </>)}
                                    
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </div>


        </div>
    );
};

export default UnitPage;