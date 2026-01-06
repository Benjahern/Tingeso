import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import loanService from '../../../services/loans.service';
import unitService from '../../../services/unit.service';
import clientService from '../../../services/client.service';
import Card from 'react-bootstrap/Card';
import { Button, Modal, Form } from 'react-bootstrap';
import Table from 'react-bootstrap/Table';
import ReturnLoanModal from '../../common/ReturnLoanModal';

const LoanDetails = () => {
    const { loanId } = useParams();
    const [loan, setLoan] = useState(null);
    const [returnCondition, setReturnCondition] = useState({});
    const [chargeDamages, setChargeDamages] = useState({});
    const [customDamage, setCustomDamage] = useState({});
    const [showReturnModal, setShowReturnModal] = useState(false);
    const [unitsMap, setUnitsMap] = useState({});
    const [client, setClient] = useState(null);

    const navigate = useNavigate();

    const init = () => {
        loanService.getLoanById(loanId).then(response => {
            console.log('Printing loan data', response.data);
            const loanData = response.data;
            setLoan(loanData);

            const initialConditions = {};
            const unitPromises = [];

            loanData.loanUnits?.forEach(loanUnit => {
                // Correctly access unitId directly from loanUnit object
                initialConditions[loanUnit.unitId] = '';
                // Fetch unit details
                unitPromises.push(unitService.getUnitById(loanUnit.unitId));
            });
            setReturnCondition(initialConditions);

            // Resolve all unit promises
            Promise.all(unitPromises).then(responses => {
                const map = {};
                responses.forEach(res => {
                    map[res.data.unitId] = res.data;
                });
                setUnitsMap(map);
            }).catch(err => console.error("Error loading units:", err));

            // Fetch Client Details
            if (loanData.clientId) {
                clientService.getClient(loanData.clientId)
                    .then(res => {
                        setClient(res.data);
                    })
                    .catch(err => console.error("Error loading client:", err));
            }

        }).catch(error => {
            console.error('Something went wrong', error);
            if (error.response) {
                console.error('Server response:', error.response.status, error.response.data);
            } else {
                console.error('Error message:', error.message);
            }
        });
    };
    useEffect(() => {
        init();
    }, [loanId]);

    const handleBack = () => {
        navigate('/loans');
    };

    const handleDelete = () => {
        const confirmDelete = window.confirm('¿Está seguro de que desea eliminar este préstamo? Esta acción no se puede deshacer.');
        if (!confirmDelete) {
            return;
        }
        loanService.remove(loanId).then(response => {
            console.log('Loan deleted successfully', response.data);
            navigate('/loans');
        }).catch(error => {
            console.error('Something went wrong', error);
            if (error.response) {
                console.error('Server response:', error.response.status, error.response.data);
                alert('Error: ' + (error.response.data?.error || JSON.stringify(error.response.data)));
            } else {
                console.error('Error message:', error.message);
                alert('Error: ' + error.message);
            }
        });
    };

    const handleShowReturnModal = () => {
        setShowReturnModal(true);
    };

    const handleCloseReturnModal = () => {
        setShowReturnModal(false);
    };

    const handleConditionChange = (unitId, condition) => {
        setReturnCondition(prevConditions => ({
            ...prevConditions,
            [unitId]: condition
        }));

        if (condition !== 'Regular') {
            setChargeDamages(prev => ({ ...prev, [unitId]: false }));
            setCustomDamage(prev => ({ ...prev, [unitId]: 0 }));
        }
    };

    const handleChargeDamage = (unitId, shouldCharge) => {
        setChargeDamages(prev => ({ ...prev, [unitId]: shouldCharge }));

        if (!shouldCharge) {
            setCustomDamage(prev => ({ ...prev, [unitId]: 0 }));
        }
    };

    const handleCustomDamageChange = (unitId, amount, maxAmount) => {
        const numAmount = parseFloat(amount) || 0;
        if (numAmount <= maxAmount) {
            setCustomDamage(prev => ({ ...prev, [unitId]: numAmount }));
        }
    };

    const handleReturnLoan = () => {
        const allConditionsFilled = Object.values(returnCondition).every(cond => cond.trim() !== '');

        if (!allConditionsFilled) {
            alert('Por favor, complete las condiciones de todas las unidades antes de devolver el préstamo.');
            return;
        }

        const payload = {
            unitCondition: returnCondition,
            customDamages: customDamage
        };

        loanService.returnLoan(loanId, payload).then(response => {
            console.log('Loan returned successfully', response.data);
            const returnedLoan = response.data;
            const fine = returnedLoan.fine || 0;

            const damagedAmount = loan.loanUnits.reduce((acc, loanUnit) => {
                const condition = returnCondition[loanUnit.unitId];
                if (condition === 'Dañado') {
                    const unit = unitsMap[loanUnit.unitId];
                    return acc + (unit?.tool?.replacementValue || 0);
                }
                if (condition === 'Regular' && chargeDamages[loanUnit.unitId]) {
                    const customAmount = parseFloat(customDamage[loanUnit.unitId]) || 0;
                    return acc + customAmount;
                }
                return acc;
            }, 0);

            const total = returnedLoan.price + fine + damagedAmount;

            let message = '✓ Devolución registrada exitosamente\n\n';
            message += `Préstamo #${returnedLoan.loanId}\n`;
            message += `Monto: ${new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(returnedLoan.price)}\n`;

            if (fine > 0) {
                message += `Multa por atraso: ${new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(fine)}\n`;
            }

            if (damagedAmount > 0) {
                message += `Valor de reemplazo (herramientas dañadas): ${new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(damagedAmount)}\n`;
            }

            message += `\nTotal: ${new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(total)}`;

            alert(message);

            handleCloseReturnModal();
            init();
        }).catch(error => {
            console.log('Something went wrong', error);
            console.error('Error details:', error.response ? error.response.data : error.message);
        });
    };

    if (!loan) {
        return (
            <div className="container mt-5">
                <div className="text-center">
                    <div className="spinner-border" role="status">
                        <span className="visually-hidden">Cargando...</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <Card>
                <Card.Header as="h3"> Detalles del prestamo {loanId} {" "}
                    <span className={'badge ' + (loan && loan.active ? 'bg-success' : 'bg-secondary')}>
                        {loan && (loan.active ? 'Activo' : 'Inactivo')}
                    </span>
                </Card.Header>
                <Card.Body>

                    <Table striped bordered hover responsive>
                        <thead>
                            <tr>
                                <th>Cliente</th>
                                <th>RUT</th>
                                <th>Fecha de inicio</th>
                                <th>Fecha de fin</th>
                                <th>Precio</th>
                            </tr>
                        </thead>
                        <tbody>

                            <tr>
                                <td>{client ? client.name : 'Cargando...'}</td>
                                <td>{client ? client.rut : 'Cargando...'}</td>
                                <td>{new Date(loan.loanStart).toLocaleDateString()}</td>
                                <td>{new Date(loan.loanEnd).toLocaleDateString()}</td>
                                <td>{new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(loan.price)}</td>
                            </tr>
                            <tr>
                                <td colSpan="5">
                                    <strong>Deuda Actual del Cliente: </strong>
                                    {client ? new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(client.debt) : 'Cargando...'}
                                </td>
                            </tr>
                        </tbody>
                    </Table>


                    <hr />

                    <h5>Herramientas del Préstamo</h5>
                    {loan.loanUnits && loan.loanUnits.length > 0 ? (

                        <Table striped bordered hover responsive>
                            <thead>
                                <tr>
                                    <th>Herramienta</th>
                                    <th>Unidad ID</th>
                                    <th>Estado</th>


                                </tr>
                            </thead>
                            <tbody>
                                {loan.loanUnits.map((loanUnit) => {
                                    const unit = unitsMap[loanUnit.unitId];
                                    return (
                                        <tr key={loanUnit.loanUnitId}>
                                            <td>{unit ? unit.tool?.toolName : 'Cargando...'}</td>
                                            <td>{loanUnit.unitId}</td>
                                            <td>{unit ? unit.condition : 'Cargando...'}</td>
                                        </tr>
                                    )
                                })}
                            </tbody>
                        </Table>
                    ) : (
                        <p>No hay herramientas asociadas a este préstamo.</p>
                    )}


                    <div className="mt-4">
                        <Button variant="secondary" onClick={handleBack} className="me-2">Volver</Button>

                        {loan.active && (
                            <Button variant="success" onClick={handleShowReturnModal} className="me-2">Devolver Préstamo</Button>
                        )}

                        <Button variant="danger" onClick={handleDelete}>Eliminar Préstamo</Button>
                    </div>
                </Card.Body>
            </Card>
            <ReturnLoanModal
                show={showReturnModal}
                onHide={handleCloseReturnModal}
                loan={loan}
                client={client}
                returnCondition={returnCondition}
                chargeDamages={chargeDamages}
                customDamage={customDamage}
                onConditionChange={handleConditionChange}
                onChargeDamageChange={handleChargeDamage}
                onCustomDamageChange={handleCustomDamageChange}
                onConfirmReturn={handleReturnLoan}
                unitsMap={unitsMap}
            />
        </div >

    );
};

export default LoanDetails;