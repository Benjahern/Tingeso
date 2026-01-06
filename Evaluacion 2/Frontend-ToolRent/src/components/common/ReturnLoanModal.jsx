import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Alert, Table } from 'react-bootstrap';

const ReturnLoanModal = ({
  show,
  onHide,
  loan,
  client,
  returnCondition,
  chargeDamages,
  customDamage,
  onConditionChange,
  onChargeDamageChange,
  onCustomDamageChange,
  onConfirmReturn,
  unitsMap
}) => {
  const [daysLate, setDaysLate] = useState(0);
  const [fineAmount, setFineAmount] = useState(0);
  const [totalAmount, setTotalAmount] = useState(0);

  useEffect(() => {
    if (loan && show) {
      calculateFine();
    }
  }, [loan, show, returnCondition, chargeDamages, customDamage]);

  const calculateFine = () => {
    const today = new Date();
    const dueDate = new Date(loan.loanEnd);

    if (today > dueDate) {
      const diffTime = Math.abs(today - dueDate);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      setDaysLate(diffDays);

      const fine = loan.fine || (diffDays * (loan.store?.dailyFine || 0));
      setFineAmount(fine);

      const damageAmount = calculateDamageFees();

      setTotalAmount(loan.price + fine + damageAmount);
    } else {
      setDaysLate(0);
      setFineAmount(loan.fine || 0);
      const damageAmount = calculateDamageFees();
      setTotalAmount(loan.price + damageAmount);
    }
  };

  const handleConditionChange = (unitId, value) => {
    onConditionChange(unitId, value);
  };

  const handleChargeDamage = (unitId, shouldCharge) => {
    onChargeDamageChange(unitId, shouldCharge);
  };

  const handleCustomDamageChange = (unitId, amount, maxAmount) => {
    onCustomDamageChange(unitId, amount, maxAmount);
  };


  const calculateDamageFees = () => {
    if (!loan.loanUnits) return 0;

    return loan.loanUnits.reduce((acc, loanUnit) => {
      const unitId = loanUnit.unitId;
      const condition = returnCondition[unitId];
      const unit = unitsMap ? unitsMap[unitId] : null;

      if (condition === 'Dañado') {
        return acc + (unit?.tool?.replacementValue || 0);
      }
      if (condition === 'Regular' && chargeDamages[unitId]) {
        const customAmount = parseFloat(customDamage[unitId]) || 0;
        return acc + customAmount;
      }
      return acc;
    }, 0);
  };

  const damageAmount = calculateDamageFees();

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP'
    }).format(amount);
  };

  if (!loan) return null;

  return (
    <Modal show={show} onHide={onHide} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>Devolver Préstamo #{loan.loanId}</Modal.Title>
      </Modal.Header>

      <Modal.Body>
        {/* Información del préstamo */}
        <Alert variant="info">
          <h6 className="mb-3"><strong>Información del Préstamo</strong></h6>
          <p className="mb-1"><strong>Cliente:</strong> {client ? client.name : 'Cargando...'}</p>
          <p className="mb-1"><strong>RUT:</strong> {client ? client.rut : 'Cargando...'}</p>
          <p className="mb-1"><strong>Fecha de inicio:</strong> {new Date(loan.loanStart).toLocaleDateString()}</p>
          <p className="mb-1"><strong>Fecha de fin:</strong> {new Date(loan.loanEnd).toLocaleDateString()}</p>
        </Alert>

        {/* Resumen de pago */}
        <div className="border rounded p-3 mb-3">
          <h6 className="mb-3"><strong>Resumen de Pago</strong></h6>

          <Table borderless size="sm">
            <tbody>
              <tr>
                <td><strong>Monto del préstamo:</strong></td>
                <td className="text-end">{formatCurrency(loan.price)}</td>
              </tr>

              {daysLate > 0 && (
                <>
                  <tr className="text-danger">
                    <td><strong>Días de atraso:</strong></td>
                    <td className="text-end">{daysLate} días</td>
                  </tr>
                  <tr className="text-danger">
                    <td><strong>Multa por atraso:</strong></td>
                    <td className="text-end">{formatCurrency(fineAmount)}</td>
                  </tr>
                  <tr>
                    <td colSpan="2"><hr /></td>
                  </tr>
                </>
              )}
              {damageAmount > 0 && (
                <tr className="text-danger">
                  <td><strong>Valor de reemplazo (herramientas dañadas):</strong></td>
                  <td className="text-end">{formatCurrency(damageAmount)}</td>
                </tr>
              )}

              {(daysLate > 0 || damageAmount > 0) && (
                <tr>
                  <td colSpan="2"><hr /></td>
                </tr>
              )
              }
              <tr className="fs-5">
                <td><strong>TOTAL A PAGAR:</strong></td>
                <td className="text-end">
                  <strong className={daysLate > 0 || damageAmount > 0 ? 'text-danger' : 'text-success'}>
                    {formatCurrency(totalAmount)}
                  </strong>
                </td>
              </tr>
            </tbody>
          </Table>
        </div>

        {/* Condiciones de las herramientas */}
        <div className="mb-3">
          <h6 className="mb-3"><strong>Condición de las Herramientas</strong></h6>
          <p className="text-muted small">
            Por favor, seleccione la condición en la que se devuelve cada herramienta:
          </p>

          {loan.loanUnits?.map((loanUnit) => {
            const unitId = loanUnit.unitId;
            const unit = unitsMap ? unitsMap[unitId] : null;
            const replacementValue = unit?.tool?.replacementValue || 0;
            return (
              <Form.Group key={unitId} className="mb-3">
                <Form.Label>
                  <strong>{unit ? unit.tool?.toolName : 'Cargando...'}</strong> (Unidad {unitId})
                </Form.Label>
                <Form.Select
                  value={returnCondition[unitId] || ''}
                  onChange={(e) => handleConditionChange(unitId, e.target.value)}
                  required
                >
                  <option value="">Seleccione una condición...</option>
                  <option value="Bueno">Bueno</option>
                  <option value="Regular">Regular</option>
                  <option value="Dañado">Dañado</option>
                </Form.Select>

                {returnCondition[unitId] === 'Regular' && (
                  <Alert variant="warning" className='mt-2 mb-0'>
                    <div className="mb-2">
                      <strong> Herramienta en estado regular:</strong>
                      <p className='mb-2 small'>
                        Valor de referencia de reposición: {formatCurrency(replacementValue)}.
                      </p>
                    </div>
                    <Form.Check
                      type="checkbox"
                      id={`charge-damage-${unitId}`}
                      label="¿Cobrar por daño?"
                      checked={chargeDamages[unitId] || false}
                      onChange={(e) => handleChargeDamage(unitId, e.target.checked)}
                      className='mb-2'
                    />

                    {chargeDamages[unitId] && (
                      <div>
                        <Form.Label className='small mb-1'>
                          Monto a cobrar (máximo {formatCurrency(replacementValue)}):
                        </Form.Label>

                        <Form.Control
                          type="number"
                          min="0"
                          max={replacementValue}
                          step="100"
                          value={customDamage[unitId] || ''}
                          onChange={(e) => handleCustomDamageChange(
                            unitId,
                            e.target.value,
                            replacementValue
                          )}
                          placeholder='Ingrese el monto a cobrar'
                        />
                        {customDamage[unitId] > replacementValue && (
                          <Form.Text className='text-danger'>
                            El monto no puede exceder el valor de reposición.
                          </Form.Text>
                        )}
                      </div>
                    )}
                  </Alert>
                )}

                {returnCondition[unitId] === 'Dañado' && (
                  <Alert variant="danger" className='mt-2 mb-0'>
                    <strong> Herramienta dañada:</strong> Se cobrará el valor de reemplazo de {" "}
                    {formatCurrency(replacementValue)} por esta herramienta.
                  </Alert>
                )}
              </Form.Group>
            )
          })}
        </div>

        {daysLate > 0 && (
          <Alert variant="warning">
            <strong>Atención:</strong> Este préstamo tiene {daysLate} día(s) de atraso.
            Se aplicará una multa de {formatCurrency(fineAmount)} que será agregada a la deuda del cliente.
          </Alert>
        )}
      </Modal.Body>

      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Cancelar
        </Button>
        <Button variant="success" onClick={onConfirmReturn}>
          Confirmar Devolución
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ReturnLoanModal;
