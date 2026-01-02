package com.example.loans_service.Service;

import com.example.loans_service.Client.*;
import com.example.loans_service.Entity.LoanEntity;
import com.example.loans_service.Entity.LoanUnitEntity;
import com.example.loans_service.Models.*;
import com.example.loans_service.Repository.LoanRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepo;
    private final ClientClient clientClient;
    private final KardexClient kardexClient;
    private final WorkerClient workerClient;
    private final UnitClient unitClient;
    private final ToolClient toolClient;
    private final StoreClient storeClient;

    public LoanService(LoanRepository loanRepo, UnitClient unitClient, ClientClient clientClient,
            KardexClient kardexClient, WorkerClient workerClient, ToolClient toolClient, StoreClient storeClient) {
        this.kardexClient = kardexClient;
        this.unitClient = unitClient;
        this.toolClient = toolClient;
        this.loanRepo = loanRepo;
        this.clientClient = clientClient;
        this.workerClient = workerClient;
        this.storeClient = storeClient;
    }

    public LoanEntity getLoanById(Long id) {
        return loanRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Loan no encontrado:"));
    }

    public List<LoanEntity> getAllLoans() {
        return loanRepo.findAll();
    }

    @Transactional
    public void removeLoansById(Long id) {
        LoanEntity loansEntity = getLoanById(id);
        List<LoanUnitEntity> loanUnitsCopy = new ArrayList<>(loansEntity.getLoanUnits());
        for (LoanUnitEntity loanUnit : loanUnitsCopy) {
            try {
                UnitDto unit = unitClient.getUnitById(loanUnit.getUnitId());
                if (unit != null) {
                    unit.setStatus("Disponible");
                    unitClient.updateUnit(unit.getUnitId(), unit);
                    ToolDto tool = unit.getTool();
                    tool.setStock(tool.getStock() + 1);
                    toolClient.updateTool(tool.getToolId(), tool);
                }
            } catch (Exception e) {
                System.err.println("Error procesando LoanUnit: " + e.getMessage());
                e.printStackTrace();
            }
        }
        kardexClient.deleteByLoanId(id);
        loanRepo.deleteById(id);
    }

    public List<LoanEntity> getLoansByClientId(Long clientId) {
        return loanRepo.findByClientId(clientId);
    }

    public List<LoanEntity> getByLoanStart(LocalDate loanStart) {
        return loanRepo.findByLoanStart(loanStart);
    }

    public List<LoanEntity> getByLoanEnd(LocalDate loanEnd) {
        return loanRepo.findByLoanEnd(loanEnd);
    }

    public List<LoanEntity> getActiveBeforeDate(LocalDate date) {
        return loanRepo.findByLoanEndBeforeAndActiveTrue(date);
    }

    public List<LoanEntity> getLoansByActive() {
        return loanRepo.findByActive(true);
    }

    public List<LoanEntity> getLoansByInactive() {
        return loanRepo.findByActive(false);
    }

    public long countLoansActive(Long clientId) {
        return loanRepo.countByClientIdAndActiveTrue(clientId);
    }

    @Transactional
    public LoanEntity createLoan(Long clientId, LocalDate loanStart, LocalDate loanEnd, List<Long> toolIds,
            Long workerId) {
        WorkerDto worker = workerClient.getWorker(workerId);

        ClientDto client = clientClient.findById(clientId);

        if (countLoansActive(clientId) >= 5) {
            throw new IllegalStateException("The client have 5 loans active.");
        }

        if ("RESTRINGIDO".equalsIgnoreCase(client.getState())) {
            throw new IllegalStateException("El cliente está restringido y no puede solicitar préstamos.");
        }

        LocalDate today = LocalDate.now();
        List<LoanEntity> activeLoans = loanRepo.findByClientIdAndActiveTrue(clientId);

        for (LoanEntity loan : activeLoans) {
            if (loan.getLoanEnd().isBefore(today)) {
                throw new IllegalStateException(
                        "El cliente tiene un préstamo vencido (ID: " + loan.getLoanId() +
                                "). Fecha de vencimiento: " + loan.getLoanEnd() +
                                ". No puede solicitar nuevos préstamos hasta devolver las herramientas.");
            }
        }

        System.out.println("DEBUG: Cliente " + clientId + " tiene " + activeLoans.size() + " préstamos activos");

        Set<Long> toolsInActiveLoans = new HashSet<>();
        for (LoanEntity loan : activeLoans) {
            for (LoanUnitEntity loanUnit : loan.getLoanUnits()) {
                toolsInActiveLoans.add(unitClient.getUnitById(loanUnit.getUnitId()).getTool().getToolId());
                System.out.println("DEBUG: Herramienta en préstamo activo: "
                        + unitClient.getUnitById(loanUnit.getUnitId()).getTool().getToolId());
            }
        }

        System.out.println("DEBUG: Total de herramientas en préstamos activos: " + toolsInActiveLoans.size());
        System.out.println("DEBUG: Herramientas solicitadas: " + toolIds);

        Set<Long> duplicateToolIds = new HashSet<>(toolsInActiveLoans);
        duplicateToolIds.retainAll(toolIds);

        System.out.println("DEBUG: Herramientas duplicadas encontradas: " + duplicateToolIds);

        if (!duplicateToolIds.isEmpty()) {
            List<String> toolNames = new ArrayList<>();
            for (Long toolId : duplicateToolIds) {
                ToolDto tool = toolClient.getTool(toolId);
                toolNames.add(tool.getToolName());
            }

            throw new IllegalStateException(
                    "El cliente ya tiene préstamos activos de las siguientes herramientas: " +
                            String.join(", ", toolNames));
        }

        LoanEntity newLoan = new LoanEntity();
        newLoan.setClientId(client.getClientId());
        newLoan.setLoanStart(loanStart);
        newLoan.setLoanEnd(loanEnd);
        newLoan.setActive(true);

        Set<Long> toolIdsInLoan = new HashSet<>();
        double totalPrice = 0;
        for (Long toolId : toolIds) {

            UnitDto unit = unitClient.getFirstAvailableByToolId(toolId);

            if (!"Disponible".equalsIgnoreCase(unit.getStatus())) {
                throw new IllegalStateException("The unit is not disponible");
            }
            if ("Dañado".equalsIgnoreCase(unit.getCondition())
                    || "Dada de baja".equalsIgnoreCase(unit.getCondition())) {
                throw new IllegalStateException("La unidad " + unit.getUnitId() + " (" + unit.getTool().getToolName()
                        + ") no se puede prestar.");
            }

            if (!toolIdsInLoan.add(toolId)) {
                throw new IllegalStateException("The tool has already been added.");
            }

            double dailyPrice = unit.getTool().getDailyPrice();
            totalPrice = totalPrice + dailyPrice;
        }

        Long loanDays = ChronoUnit.DAYS.between(loanStart, loanEnd) + 1;
        if (loanDays <= 0) {
            throw new IllegalArgumentException("La fecha de término debe ser igual o posterior a la fecha de inicio.");
        }
        totalPrice = totalPrice * loanDays;
        newLoan.setPrice(totalPrice);

        // Update client debt using Feign client
        clientClient.addDebt(clientId, Map.of("amount", totalPrice));

        LoanEntity savedLoan = loanRepo.save(newLoan);

        for (Long toolId : toolIds) {
            UnitDto unit = unitClient.getFirstAvailableByToolId(toolId);
            unit.setStatus("Prestada");
            unitClient.updateUnit(unit.getUnitId(), unit);

            LoanUnitEntity newLoanUnit = new LoanUnitEntity();
            newLoanUnit.setUnitId(unit.getUnitId());
            newLoanUnit.setLoan(savedLoan);
            savedLoan.getLoanUnits().add(newLoanUnit);

            // Register movement using Feign client
            String comment = "Salida en prestamo Id:" + savedLoan.getLoanId() + " para el cliente id: " + clientId;
            KardexDto kardexDto = new KardexDto();
            kardexDto.setUnitId(unit.getUnitId());
            kardexDto.setWorkerId(workerId);
            kardexDto.setLoanId(savedLoan.getLoanId());
            kardexDto.setMovement("SALIDA_PRESTAMO");
            kardexDto.setComment(comment);
            kardexDto.setType(-1);
            kardexClient.createMovement(kardexDto);

            // Update tool stock using Feign client
            ToolDto tool = toolClient.getTool(toolId);
            tool.setStock(tool.getStock() - 1);
            toolClient.updateTool(toolId, tool);
        }

        if (countLoansActive(clientId) + 1 == 5) {
            clientClient.setState(clientId, "RESTRINGIDO");
        }

        return loanRepo.save(newLoan);
    }

    @Transactional
    public LoanEntity returnLoan(Long loanId, Long workerId, Map<Long, String> unitCondition) {

        WorkerDto worker = workerClient.getWorker(workerId);
        LoanEntity loan = getLoanById(loanId);

        if (!loan.isActive()) {
            throw new IllegalStateException("The loan is not active");
        }

        Long clientId = loan.getClientId();
        ClientDto client = clientClient.findById(clientId);

        // Get daily fine from rates-service
        Long dailyFine = storeClient.getDailyFine();

        // If a fine is needed
        LocalDate returnDate = LocalDate.now();
        LocalDate dueDate = loan.getLoanEnd();
        long totalFine = 0;
        long daysLate = 0;

        if (returnDate.isAfter(dueDate)) {
            daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            totalFine = daysLate * dailyFine;
            loan.setFine(totalFine);
            // Add fine to client debt
            clientClient.addDebt(clientId, Map.of("amount", (double) totalFine));
        }

        loan.setActive(false);
        loan.setReturnLoan(returnDate);
        double damagedCost = 0;

        for (LoanUnitEntity loanUnit : loan.getLoanUnits()) {
            Long unitId = loanUnit.getUnitId();
            UnitDto unit = unitClient.getUnitById(unitId);
            String condition = unitCondition.get(unitId);

            if (condition == null || condition.trim().isEmpty()) {
                throw new IllegalArgumentException("Falta la condición para la unidad con ID: " + unitId);
            }

            if ("Dañado".equalsIgnoreCase(condition)) {
                unit.setStatus("Dañado");
                unit.setCondition("Dañado");

                double replacementValue = unit.getTool().getReplacementValue();
                damagedCost += replacementValue;
                // Add damaged cost to client debt
                clientClient.addDebt(clientId, Map.of("amount", replacementValue));
            } else {
                unit.setStatus("Disponible");
                unit.setCondition(condition);
            }

            // Update unit via Feign client
            unitClient.updateUnit(unitId, unit);
            loanUnit.setReturnDate(returnDate);

            // Register movement using Feign client
            String comment = "Devolución del préstamo ID: " + loan.getLoanId() + ". Condición de entrega: " + condition;
            KardexDto kardexDto = new KardexDto();
            kardexDto.setUnitId(unitId);
            kardexDto.setWorkerId(workerId);
            kardexDto.setLoanId(loan.getLoanId());
            kardexDto.setMovement("ENTRADA_DEVOLUCION");
            kardexDto.setComment(comment);
            kardexDto.setType(1);
            kardexClient.createMovement(kardexDto);

            // Update tool stock using Feign client
            ToolDto tool = unit.getTool();
            tool.setStock(tool.getStock() + 1);
            toolClient.updateTool(tool.getToolId(), tool);
        }

        // Check if client can be unrestricted
        if ("RESTRINGIDO".equalsIgnoreCase(client.getState())) {
            if (countLoansActive(clientId) - 1 < 5) {
                clientClient.setState(clientId, "ACTIVO");
            }
        }

        return loanRepo.save(loan);
    }

    // Function for updating clients if they are late
    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight
    @Transactional
    public void checkClient() {
        LocalDate today = LocalDate.now();

        List<LoanEntity> loans = loanRepo.findByLoanEndBeforeAndActiveTrue(today);

        for (LoanEntity loan : loans) {
            Long clientId = loan.getClientId();
            ClientDto client = clientClient.findById(clientId);

            long daysOverdue = ChronoUnit.DAYS.between(loan.getLoanEnd(), today);
            if (daysOverdue > 0) {
                Long dailyFine = storeClient.getDailyFine();
                long totalFine = dailyFine * daysOverdue;
                loan.setFine(totalFine);
                loanRepo.save(loan);
            }

            if (!"RESTRINGIDO".equalsIgnoreCase(client.getState())) {
                clientClient.setState(clientId, "RESTRINGIDO");
            }
        }
    }

    @PostConstruct
    public void initCheck() {
        checkClient();
    }

    public List<ClientDto> getClientsWithFine(Long fine) {
        List<LoanEntity> loansWithFine = loanRepo.findByFineIsGreaterThan(fine);
        return loansWithFine.stream()
                .map(loan -> clientClient.findById(loan.getClientId()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<LoanEntity> getLoansByClientRut(String rut) {
        // Get clients matching the rut from clients-service
        List<ClientDto> clients = clientClient.findByRut(rut);
        if (clients.isEmpty()) {
            return new ArrayList<>();
        }
        // Get all loans for those client IDs
        List<Long> clientIds = clients.stream()
                .map(ClientDto::getClientId)
                .collect(Collectors.toList());

        return loanRepo.findAll().stream()
                .filter(loan -> clientIds.contains(loan.getClientId()))
                .collect(Collectors.toList());
    }

    public List<LoanEntity> getLoansByClientName(String name) {
        // Get clients matching the name from clients-service
        List<ClientDto> clients = clientClient.findByName(name);
        if (clients.isEmpty()) {
            return new ArrayList<>();
        }
        // Get all loans for those client IDs
        List<Long> clientIds = clients.stream()
                .map(ClientDto::getClientId)
                .collect(Collectors.toList());

        return loanRepo.findAll().stream()
                .filter(loan -> clientIds.contains(loan.getClientId()))
                .collect(Collectors.toList());
    }

    public List<LoanEntity> getActiveLoansByClientRut(String rut) {
        // Get clients matching the rut from clients-service
        List<ClientDto> clients = clientClient.findByRut(rut);
        if (clients.isEmpty()) {
            return new ArrayList<>();
        }
        // Get active loans for those client IDs
        List<Long> clientIds = clients.stream()
                .map(ClientDto::getClientId)
                .collect(Collectors.toList());

        return loanRepo.findAll().stream()
                .filter(loan -> loan.isActive() && clientIds.contains(loan.getClientId()))
                .collect(Collectors.toList());
    }

}
