package com.example.reports_service.Service;

import com.example.reports_service.Client.*;
import com.example.reports_service.Models.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportsService {

    private final KardexClient kardexClient;
    private final InventoryClient inventoryClient;
    private final ClientsClient clientsClient;
    private final LoansClient loansClient;

    public ReportsService(KardexClient kardexClient, InventoryClient inventoryClient,
            ClientsClient clientsClient, LoansClient loansClient) {
        this.kardexClient = kardexClient;
        this.inventoryClient = inventoryClient;
        this.clientsClient = clientsClient;
        this.loansClient = loansClient;
    }

    /**
     * Obtiene el ranking de herramientas más prestadas, enriquecido con nombres.
     * Llama a kardex-service para obtener el ranking por unitId y luego a
     * inventory-service para obtener el nombre de la herramienta.
     */
    public List<RankingItemDto> getToolRanking(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener ranking básico de kardex-service
        List<Object[]> rawRanking = kardexClient.getRanking(fechaInicio, fechaFin);

        java.util.Map<String, RankingItemDto> aggregationMap = new java.util.HashMap<>();

        for (Object[] item : rawRanking) {
            Long unitId = ((Number) item[0]).longValue();
            Long totalSolicitudes = ((Number) item[1]).longValue();

            // Obtener información de la unidad desde inventory-service
            try {
                UnitDto unit = inventoryClient.getUnitById(unitId);
                String toolName = unit.getTool().getToolName();
                String category = unit.getTool().getCategory();

                if (aggregationMap.containsKey(toolName)) {
                    RankingItemDto existing = aggregationMap.get(toolName);
                    existing.setTotalSolicitudes(existing.getTotalSolicitudes() + totalSolicitudes);
                } else {
                    RankingItemDto dto = new RankingItemDto();
                    dto.setUnitId(unitId); // Guardamos un unitId de referencia, aunque ya no es único
                    dto.setToolName(toolName);
                    dto.setCategory(category);
                    dto.setTotalSolicitudes(totalSolicitudes);
                    aggregationMap.put(toolName, dto);
                }

            } catch (Exception e) {
                // Si no se puede obtener la info, agregar como desconocido (sin agrupar por
                // nombre si es desconocido, o agrupar por "Desconocido")
                // En este caso, lo dejamos separado para no mezclar errores
                RankingItemDto dto = new RankingItemDto();
                dto.setUnitId(unitId);
                dto.setToolName("Herramienta no encontrada");
                dto.setCategory("N/A");
                dto.setTotalSolicitudes(totalSolicitudes);
                // Usamos unitId como clave para no colapsar todos los errores
                aggregationMap.put("UNKNOWN_" + unitId, dto);
            }
        }

        List<RankingItemDto> result = new ArrayList<>(aggregationMap.values());
        // Ordenar por total de solicitudes descendente
        result.sort((a, b) -> b.getTotalSolicitudes().compareTo(a.getTotalSolicitudes()));

        return result;
    }

    /**
     * Obtiene todos los clientes con deuda pendiente.
     */
    public List<ClientDebtDto> getClientsWithDebt() {
        return clientsClient.getClientsWithDebt();
    }

    /**
     * Obtiene todos los préstamos activos con información enriquecida del cliente
     * y las herramientas prestadas.
     */
    public List<ActiveLoanDto> getActiveLoansReport() {
        List<LoanDto> activeLoans = loansClient.getActiveLoans();
        List<ActiveLoanDto> report = new ArrayList<>();

        for (LoanDto loan : activeLoans) {
            ActiveLoanDto dto = new ActiveLoanDto();
            dto.setLoanId(loan.getLoanId());
            dto.setLoanStart(loan.getLoanStart());
            dto.setLoanEnd(loan.getLoanEnd());

            // Calcular días restantes
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), loan.getLoanEnd());
            dto.setDaysRemaining(daysRemaining);
            dto.setOverdue(daysRemaining < 0);

            // Obtener información del cliente
            try {
                ClientDebtDto client = clientsClient.getClientById(loan.getClientId());
                dto.setClientName(client.getName());
                dto.setClientRut(client.getRut());
            } catch (Exception e) {
                dto.setClientName("Cliente no encontrado");
                dto.setClientRut("N/A");
            }

            // Obtener nombres de las herramientas prestadas
            List<String> toolNames = new ArrayList<>();
            if (loan.getLoanUnits() != null) {
                for (LoanUnitDto loanUnit : loan.getLoanUnits()) {
                    try {
                        UnitDto unit = inventoryClient.getUnitById(loanUnit.getUnitId());
                        toolNames.add(unit.getTool().getToolName());
                    } catch (Exception e) {
                        toolNames.add("Herramienta #" + loanUnit.getUnitId());
                    }
                }
            }
            dto.setToolNames(toolNames);

            report.add(dto);
        }

        return report;
    }
}
