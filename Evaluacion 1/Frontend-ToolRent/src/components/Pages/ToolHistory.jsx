import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toolService from "../../services/tool.service";
import Table from 'react-bootstrap/Table';
import kardexService from '../../services/kardex.service';
import DateRangeFilter from '../common/DateRangeFilter';
import KardexExcel from '../common/KardexExcel';
import Button from 'react-bootstrap/esm/Button';
import { FileEarmarkSpreadsheet } from 'react-bootstrap-icons';


const ToolHistory = () => {
    const [history, setHistory] = useState([]);

    const navigate = useNavigate();
    const { toolId } = useParams();
    const { exportToExcel } = KardexExcel();

    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);

    const [filteredHistory, setFilteredHistory] = useState([]);

    const clearDateFilter = () => {
        setStartDate(null);
        setEndDate(null);
    };

    const init = () => {
        kardexService.getKardexByToolId(toolId).then(response => {
            console.log('Printing tool history data', response.data);
            setHistory(response.data);
            setFilteredHistory(response.data);
        }).catch(error => {
            console.log('Something went wrong', error);
        });
    };

    useEffect(() => {
        if (!startDate || !endDate) {
            setFilteredHistory(history);
            return;
        }

        const filtered = history.filter(record => {
            const recordDate = new Date(record.date);
            const recordDateOnly = new Date(recordDate.getFullYear(), recordDate.getMonth(), recordDate.getDate());
            const startDateOnly = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
            const endDateOnly = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
            
            return recordDateOnly >= startDateOnly && recordDateOnly <= endDateOnly;
        });
        
        setFilteredHistory(filtered);
    }, [startDate, endDate, history]); 

    useEffect(() => {
        init();
    }, [toolId]);

    const handleExport = () => {
        const dataToExport = filteredHistory.map(record => ({
            kardexId: record.kardexId,
            movement: record.movement,
            date: new Date(record.date).toLocaleDateString(),
            unitId: record.unit?.unitId || 'N/A',
            stockBalance: record.stockBalance,
            workerName: record.worker?.name || 'N/A',
            comment: record.comment || '-'
        }));

        const headers = {
            kardexId: 'ID Movimiento',
            movement: 'Tipo de Movimiento',
            date: 'Fecha',
            unitId: 'Unidad Asociada',
            stockBalance: 'Stock',
            workerName: 'Trabajador',
            comment: 'Comentario'
        };
        


        exportToExcel(
            dataToExport,
            `Historial_Herramienta_${toolId}`,
            'Historial',
            headers
        );
    };

    return (
        <div className='container mt-4'>
            <h2>Historial de la Herramienta</h2>
            <Button variant="success" className="mb-3" onClick={handleExport} disabled={filteredHistory.length === 0}>
                <FileEarmarkSpreadsheet className="me-2" />
                Exportar a Excel
            </Button>
            <div className='mb-3 d-flex justify-content-center'>
                <DateRangeFilter
                    startDate={startDate}
                    endDate={endDate}
                    onStartDateChange={setStartDate}  
                    onEndDateChange={setEndDate}      
                    onClear={clearDateFilter}         
                />
            </div>
            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>ID Movimiento</th>
                        <th>Tipo de Movimiento</th>
                        <th>Fecha</th>
                        <th>Unidad Asociada</th>
                        <th>Stock</th>
                        <th>Trabajador</th>
                        <th>Comentario</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredHistory.map(record => (
                        <tr key={record.kardexId}>
                            <td>{record.kardexId}</td>
                            <td>{record.movement}</td>
                            <td>{new Date(record.date).toLocaleDateString()}</td>
                            <td>{record.unit?.unitId}</td>
                            <td>{record.stockBalance}</td>
                            <td>{record.worker?.name}</td>
                            <td>{record.comment}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </div>
    );
}








export default ToolHistory;