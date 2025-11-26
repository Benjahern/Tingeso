import { useCallback } from 'react';
import * as XLSX from 'xlsx';


const KardexExcel = () => {
 
    const exportToExcel = useCallback((
        data, 
        fileName = 'export', 
        sheetName = 'Datos',
        columnWidths = null,
        headers = null
    ) => {
        if (!data || data.length === 0) {
            console.warn('No hay datos para exportar');
            return;
        }

        try {
            // Si se proporcionan headers personalizados, transformar los datos
            let dataToExport = data;
            if (headers) {
                dataToExport = data.map(row => {
                    const newRow = {};
                    Object.keys(headers).forEach(key => {
                        newRow[headers[key]] = row[key];
                    });
                    return newRow;
                });
            }

            // Crear hoja de cálculo
            const worksheet = XLSX.utils.json_to_sheet(dataToExport);
            const workbook = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(workbook, worksheet, sheetName);

            // Aplicar anchos de columna si se proporcionan
            if (columnWidths && Array.isArray(columnWidths)) {
                worksheet['!cols'] = columnWidths.map(width => ({ wch: width }));
            } else {
                // Calcular anchos automáticamente basándose en el contenido
                const columns = Object.keys(dataToExport[0] || {});
                worksheet['!cols'] = columns.map(col => {
                    const maxLength = Math.max(
                        col.length,
                        ...dataToExport.map(row => 
                            String(row[col] || '').length
                        )
                    );
                    return { wch: Math.min(maxLength + 2, 50) }; // Max 50 caracteres
                });
            }

            // Generar nombre de archivo con timestamp
            const timestamp = new Date().toISOString().split('T')[0];
            const fullFileName = `${fileName}_${timestamp}.xlsx`;

            // Descargar archivo
            XLSX.writeFile(workbook, fullFileName);
            
            console.log(`Archivo exportado: ${fullFileName}`);
            return true;
        } catch (error) {
            console.error('Error al exportar a Excel:', error);
            return false;
        }
    }, []);

    return { exportToExcel };
};

export default KardexExcel;
