import  React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import loanService from '../../../services/loans.service';
import Card from 'react-bootstrap/Card';
import { Button, Modal, Form } from 'react-bootstrap';
import Table from 'react-bootstrap/Table';
import ReturnLoanModal from '../../common/ReturnLoanModal';

const LoanDetails = () => {
    const { loanId } = useParams();
    const [loan, setLoan] = useState(null);
    const [returnCondition, setReturnCondition] = useState({});
    const [showReturnModal, setShowReturnModal] = useState(false);

    const navigate = useNavigate();

    const init = () => {
        loanService.getLoanById(loanId).then(response => {
            console.log('Printing loan data', response.data);
            setLoan(response.data);

            const initialConditions = {};
            response.data.loanUnits?.forEach(loanUnit => {
                initialConditions[loanUnit.unit.unitId] = '';
            });
            setReturnCondition(initialConditions);
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
    };

    const handleReturnLoan = () => {
        const allConditionsFilled = Object.values(returnCondition).every(cond => cond.trim() !== '');

        if (!allConditionsFilled) {
            alert('Por favor, complete las condiciones de todas las unidades antes de devolver el préstamo.');
            return;
        }

        loanService.returnLoan(loanId, returnCondition).then(response => {
            console.log('Loan returned successfully', response.data);
            const returnedLoan = response.data;
            const fine = returnedLoan.fine || 0;

            const damagedAmount = loan.loanUnits.reduce((acc, loanUnit) => {
                const condition = returnCondition[loanUnit.unit.unitId];
                if (condition === 'Dañado') {
                    return acc + (loanUnit.unit.tool?.replacementValue || 0);
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
                                <td>{loan.client.name}</td>
                                <td>{loan.client.rut}</td>
                                <td>{new Date(loan.loanStart).toLocaleDateString()}</td>
                                <td>{new Date(loan.loanEnd).toLocaleDateString()}</td>
                                <td>{new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(loan.price)}</td>
                                
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
                                {loan.loanUnits.map((loanUnit) => (
                                        <tr key={loanUnit.id}>
                                            <td>{loanUnit.unit.tool.toolName}</td>
                                            <td>{loanUnit.unit.unitId}</td>
                                            <td>{loanUnit.unit.condition}</td>
                                        </tr>
                                    ))}
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
                returnCondition={returnCondition}
                onConditionChange={handleConditionChange}
                onConfirmReturn={handleReturnLoan}
            />
        </div>
    
    );
};

export default LoanDetails;