import React from 'react';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { Calendar, XCircle } from "react-bootstrap-icons";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const DateRangeFilter = ({ 
  startDate, 
  endDate, 
  onStartDateChange, 
  onEndDateChange,
  onClear,
  showActiveIndicator = true,
  label = "Rango de fechas",
  minDate = null,
  minEndDate = null
}) => {

  const calculatedMinEndDate = startDate ? 
    new Date(new Date(startDate).getTime() + 86400000) 
    : minEndDate || minDate;


  return (
    <>
      <Form.Group>
        <Form.Label>{label}</Form.Label>
        <div className="d-flex gap-2 align-items-center">
          <DatePicker
            selected={startDate}
            onChange={onStartDateChange}
            selectsStart
            startDate={startDate}
            endDate={endDate}
            minDate ={minDate}
            placeholderText="Desde"
            dateFormat="dd/MM/yyyy"
            className="form-control"
            isClearable
          />
          <span>-</span>
          <DatePicker
            selected={endDate}
            onChange={onEndDateChange}
            selectsEnd
            startDate={startDate}
            endDate={endDate}
            minDate={calculatedMinEndDate}
            placeholderText="Hasta"
            dateFormat="dd/MM/yyyy"
            className="form-control"
            isClearable
          />
        </div>
      </Form.Group>

      {/* Indicador de filtro activo */}
      {showActiveIndicator && (startDate || endDate) && (
        <div className="alert alert-info d-flex justify-content-between align-items-center mb-3 mt-3">
          <span>
            <Calendar className="me-2" />
            <strong>Filtro de fecha activo:</strong>{' '}
            {startDate && `Desde: ${startDate.toLocaleDateString('es-ES')}`}
            {startDate && endDate && ' | '}
            {endDate && `Hasta: ${endDate.toLocaleDateString('es-ES')}`}
          </span>
          <Button 
            variant="outline-danger" 
            size="sm" 
            onClick={onClear}
          >
            <XCircle className="me-1" />
            Limpiar filtro
          </Button>
        </div>
      )}
    </>
  );
};

export default DateRangeFilter;
