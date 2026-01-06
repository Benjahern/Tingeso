import kardexService from "../../services/kardex.service";
import React, { useState, useEffect } from "react";
import DateRangeFilter from "./DateRangeFilter";
import "../Style/ToolRanking.css";

const ToolRanking = () => {
  const [tools, setTools] = useState([]);
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  // Inicializar con el mes actual
  useEffect(() => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    setStartDate(firstDay);
    setEndDate(lastDay);
  }, []); // Solo al montar

  // Ejecutar cuando cambien las fechas
  useEffect(() => {
    if (startDate && endDate) {
      console.log('🔍 Fechas cambiadas:', {
        inicio: startDate.toISOString().split('T')[0],
        fin: endDate.toISOString().split('T')[0]
      });
      fetchRanking();
    }
  }, [startDate, endDate]);

  const fetchRanking = async () => {
    try {
      const params = {};

      if (startDate) {
        params.fechaInicio = startDate.toISOString().split('T')[0];
      }
      if (endDate) {
        params.fechaFin = endDate.toISOString().split('T')[0];
      }


      const response = await kardexService.getRankingHerramientas(params);


      if (response.data && Array.isArray(response.data)) {
        const formattedData = response.data.map(item => ({
          toolName: item.toolName,
          cantidad: item.totalSolicitudes
        }));
        setTools(formattedData);
      } else {
        setTools([]);
      }
    } catch (err) {
      setTools([]);
    }
  };

  const handleClearFilter = () => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    setStartDate(firstDay);
    setEndDate(lastDay);
  };

  return (
    <div className="tool-ranking-container">
      <h2>Ranking de Herramientas Más Solicitadas</h2>

      <DateRangeFilter
        startDate={startDate}
        endDate={endDate}
        onStartDateChange={setStartDate}
        onEndDateChange={setEndDate}
        onClear={handleClearFilter}
      />

      {tools.length === 0 ? (
        <p>No hay datos disponibles para el período seleccionado</p>
      ) : (
        <>
          <table>
            <thead>
              <tr>
                <th>Posición</th>
                <th>Herramienta</th>
                <th>Solicitudes</th>
              </tr>
            </thead>
            <tbody>
              {tools.map((herramienta, index) => (
                <tr key={index}>
                  <td>#{index + 1}</td>
                  <td>{herramienta.toolName}</td>
                  <td>{herramienta.cantidad}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="ranking-summary">
            <p><strong>Total de herramientas:</strong> {tools.length}</p>
            <p>
              <strong>Total de solicitudes:</strong>{' '}
              {tools.reduce((sum, t) => sum + t.cantidad, 0)}
            </p>
          </div>
        </>
      )}
    </div>
  );
};

export default ToolRanking;
