package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.*;
import com.example.Backend_ToolRent.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoansService {

    private final LoansRepository loansRepo;
    private final UnitRepository unitRepo;
    private final ClientRepository clientRepo;
    private final WorkerRepository workerRepo;
    private final KardexService kardexService;
    private final StoreRepository storeRepo;
    private final ToolRepository toolRepo;
    private final UnitService unitService;
    private final WorkerService workerService;
    private final ToolService toolService;
    private final KardexRepository kardexRepo;


    public LoansService(LoansRepository loansRepo, ToolRepository toolRepo, StoreRepository storeRepo, UnitRepository unitRepo, ClientRepository clientRepo, WorkerRepository workerRepo, KardexRepository kardexRepo, KardexService kardexService, ToolService toolService, WorkerService workerService, UnitService unitService) {
        this.loansRepo = loansRepo;
        this.unitRepo = unitRepo;
        this.storeRepo = storeRepo;
        this.unitService = unitService;
        this.workerService = workerService;
        this.clientRepo = clientRepo;
        this.workerRepo = workerRepo;
        this.toolRepo = toolRepo;
        this.kardexService = kardexService;
        this.toolService = toolService;
        this.kardexRepo = kardexRepo;
    }

    public LoansEntity getLoansById(Long id) {
        return loansRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("LoansEntity not found"));
    }

    public List<LoansEntity> getAllLoans() {
        return loansRepo.findAll();
    }

    @Transactional
    public void removeLoansById(Long id) {
        LoansEntity loansEntity = getLoansById(id);
        List<LoanUnitEntity> loanUnitsCopy = new ArrayList<>(loansEntity.getLoanUnits());
        for (LoanUnitEntity loanUnit : loanUnitsCopy) {
            try {
                UnitEntity unit = loanUnit.getUnit();
                if (unit != null) {
                    unit.setStatus("Disponible");
                    unitRepo.save(unit);
                    ToolEntity tool = unit.getTool();
                    tool.setStock(tool.getStock() + 1);
                    toolRepo.save(tool);

                }
            } catch (Exception e) {
                System.err.println("Error procesando LoanUnit: " + e.getMessage());
                e.printStackTrace();
            }
        }
        kardexRepo.deleteByLoan_LoanId(id);
        loansRepo.delete(loansEntity);
    }

    public List<LoansEntity> getLoansByClientId(Long userId) {
        return loansRepo.findByClient_UserId(userId);
    }

    public List<LoansEntity> getByLoanStart(LocalDate loanStart) {
        return loansRepo.findByLoanStart(loanStart);
    }

    public List<LoansEntity> getByLoanEnd(LocalDate loanEnd) {
        return loansRepo.findByLoanEnd(loanEnd);
    }

    public List<LoansEntity> getActiveBeforeDate(LocalDate date) {
        return loansRepo.findByLoanEndBeforeAndActiveTrue(date);
    }

    public List<LoansEntity> getLoansByActive() {
        return loansRepo.findByActive(true);
    }

    public List<LoansEntity> getLoansByInactive() {
        return loansRepo.findByActive(false);
    }

    public long countLoansActive(Long clientId) {
        return loansRepo.countByClient_UserIdAndActiveTrue(clientId);
    }

    @Transactional
    public LoansEntity createLoan(Long clientId, Long storeId, LocalDate loanStart, LocalDate loanEnd, List<Long> toolIds, Long workerId) {
        WorkerEntity worker = workerRepo.findById(workerId).orElseThrow(() -> new EntityNotFoundException("WorkerEntity not found"));

        ClientEntity client = clientRepo.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + clientId));

        if (countLoansActive(clientId) >= 5) {
            throw new IllegalStateException("The client have 5 loans active.");

        }

        if ("RESTRINGIDO".equalsIgnoreCase(client.getState())) {
            throw new IllegalStateException("El cliente está restringido y no puede solicitar préstamos.");
        }

        LocalDate today = LocalDate.now();
        List<LoansEntity> activeLoans = loansRepo.findByClient_UserIdAndActiveTrue(clientId);

        for (LoansEntity loan : activeLoans) {
            if (loan.getLoanEnd().isBefore(today)) {
                throw new IllegalStateException(
                        "El cliente tiene un préstamo vencido (ID: " + loan.getLoanId() +
                                "). Fecha de vencimiento: " + loan.getLoanEnd() +
                                ". No puede solicitar nuevos préstamos hasta devolver las herramientas."
                );
            }
        }

        System.out.println("DEBUG: Cliente " + clientId + " tiene " + activeLoans.size() + " préstamos activos");

        Set<Long> toolsInActiveLoans = new HashSet<>();
        for (LoansEntity loan : activeLoans) {
            for (LoanUnitEntity loanUnit : loan.getLoanUnits()) {
                toolsInActiveLoans.add(loanUnit.getUnit().getTool().getToolId());
                System.out.println("DEBUG: Herramienta en préstamo activo: " + loanUnit.getUnit().getTool().getToolId());
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
                ToolEntity tool = toolRepo.findById(toolId)
                        .orElseThrow(() -> new EntityNotFoundException("ToolEntity not found"));
                toolNames.add(tool.getToolName());
            }

            throw new IllegalStateException(
                    "El cliente ya tiene préstamos activos de las siguientes herramientas: " +
                            String.join(", ", toolNames)
            );
        }

        LoansEntity newLoan = new LoansEntity();
        newLoan.setClient(client);
        newLoan.setStore(storeRepo.findById(storeId).orElseThrow(() -> new EntityNotFoundException("StoreEntity not found")));
        newLoan.setLoanStart(loanStart);
        newLoan.setLoanEnd(loanEnd);
        newLoan.setActive(true);

        Set<Long> toolIdsInLoan = new HashSet<>();
        double totalPrice = 0;
        for (Long toolId : toolIds) {

            UnitEntity unit = unitRepo.findFirstByTool_ToolIdAndStatusAndConditionNot(
                            toolId, "Disponible", "Dañado")
                    .orElseThrow(() -> new IllegalStateException(
                            "No hay unidades disponibles para la herramienta ID: " + toolId));

            if (!"Disponible".equalsIgnoreCase(unit.getStatus())) {
                throw new IllegalStateException("The unit is not disponible");
            }
            if ("Dañado".equalsIgnoreCase(unit.getCondition()) || "Dada de baja".equalsIgnoreCase(unit.getCondition())) {
                throw new IllegalStateException("La unidad " + unit.getUnitId() + " (" + unit.getTool().getToolName() + ") no se puede prestar.");
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

        client.setDebt(client.getDebt() + totalPrice);
        clientRepo.save(client);

        LoansEntity savedLoan = loansRepo.save(newLoan);

        for (Long toolId : toolIds) {
            UnitEntity unit = unitRepo.findFirstByTool_ToolIdAndStatusAndConditionNot(
                            toolId, "Disponible", "Dañado")
                    .orElseThrow(() -> new IllegalStateException(
                            "No hay unidades disponibles para la herramienta ID: " + toolId));
            unit.setStatus("Prestada");
            unitRepo.save(unit);

            LoanUnitEntity newLoanUnit = new LoanUnitEntity();
            newLoanUnit.setUnit(unit);
            newLoanUnit.setLoan(savedLoan); // Asocia al nuevo préstamo
            savedLoan.getLoanUnits().add(newLoanUnit);

            String comment = "Salida en prestamo Id:" + savedLoan.getLoanId() + " para el cliente id: " + clientId;
            kardexService.registerMovement(unit, "Salida_Prestamo", worker, savedLoan, comment);
            ToolEntity tool = toolRepo.findById(toolId).orElseThrow(() -> new EntityNotFoundException("ToolEntity not found"));
            tool.setStock(tool.getStock() - 1);


        }

        if (countLoansActive(clientId) + 1 == 5) {
            client.setState("RESTRINGIDO");
            clientRepo.save(client);
        }


        return loansRepo.save(newLoan);

    }

    public LoansEntity returnLoan(Long loanId, Long workerId, Map<Long, String> unitCondition) {

        WorkerEntity worker = workerRepo.findById(workerId).orElseThrow(() -> new EntityNotFoundException("WorkerEntity not found"));
        LoansEntity loan = getLoansById(loanId);

        if (!loan.isActive()) {
            throw new IllegalStateException("The loan is not active");
        }

        ClientEntity client = loan.getClient();
        StoreEntity store = loan.getStore();


        //If a fine is needed
        LocalDate returnDate = LocalDate.now();
        LocalDate dueDate = loan.getLoanEnd();
        long totalFine = 0;
        long dayslate = 0;

        if (returnDate.isAfter(dueDate)) {
            dayslate = ChronoUnit.DAYS.between(dueDate, returnDate);

            long dailyFine = store.getDailyFine();
            totalFine = dayslate * dailyFine;
            client.setDebt(client.getDebt() + totalFine);
            loan.setFine(totalFine);

        }
        clientRepo.save(client);
        loan.setActive(false);
        loan.setReturnLoan(returnDate);
        double damagedCost = 0;

        for (LoanUnitEntity loanUnit : loan.getLoanUnits()) {
            UnitEntity unit = loanUnit.getUnit();
            String condition = unitCondition.get(unit.getUnitId());

            if (condition == null || condition.trim().isEmpty()) {
                throw new IllegalArgumentException("Falta la condición para la unidad con ID: " + unit.getUnitId());
            }
            if ("Dañado".equalsIgnoreCase(condition)) {
                unit.setStatus("Dañado");
                unit.setCondition("Dañado");

                double replacementValue = unit.getTool().getReplacementValue();
                damagedCost += replacementValue;
                client.setDebt(client.getDebt() + replacementValue);
            } else {

                unit.setStatus("Disponible");
                unit.setCondition(condition);

            }
            unitRepo.save(unit);
            loanUnit.setReturnDate(returnDate);
            String comment = "Devolución del préstamo ID: " + loan.getLoanId() + ". Condición de entrega: " + condition;

            kardexService.registerMovement(
                    loanUnit.getUnit(),
                    "ENTRADA_DEVOLUCION",
                    worker,
                    loan,
                    comment
            );
            unit.getTool().setStock(unit.getTool().getStock() + 1);

        }
        if ("RESTRINGIDO".equalsIgnoreCase(client.getState())) {
            if (loansRepo.countByClient_UserIdAndActiveTrue(client.getUserId()) - 1 < 5) {
                client.setState("Activo");
            }
        }
        client.setDebt(client.getDebt() - totalFine - loan.getPrice());


        return loansRepo.save(loan);

    }

    public List<LoansEntity> getLoansByClientName(String name) {
        return loansRepo.findByClient_NameContainingIgnoreCase(name);
    }

    public List<LoansEntity> getLoansByClientRut(String rut) {
        return loansRepo.findByClient_RutContainingIgnoreCase(rut);
    }

    public List<LoansEntity> getActiveLoansByClientRut(String rut) {
        return loansRepo.findByClient_RutAndActiveTrue(rut);
    }

    //Fuction for the update the client if he is late
    @Scheduled(cron = "0 0 0 * * ?") // 00
    @Transactional
    public void checkClient() {
        LocalDate today = LocalDate.now();

        List<LoansEntity> loans = loansRepo.findByLoanEndBeforeAndActiveTrue(today);

        for (LoansEntity loan : loans) {
            ClientEntity client = loan.getClient();
            long daysOverdue = ChronoUnit.DAYS.between(loan.getLoanEnd(), today);
            if (daysOverdue > 0) {
                int dailyFine = loan.getStore().getDailyFine();
                BigDecimal totalFine = BigDecimal.valueOf(dailyFine);
                BigDecimal fine = totalFine.multiply(BigDecimal.valueOf(daysOverdue));
                loan.setFine(fine.longValue());
                loansRepo.save(loan);
            }
            if (!client.getState().equals("Restringido")) {
                client.setState("Restringido");
                clientRepo.save(client);
            }
        }
    }

    @PostConstruct
    public void initCheck() {
        checkClient();
    }

    public List<ClientEntity> getClientsWithFine(Long fine){
        List<LoansEntity> loansWithFine = loansRepo.findByFineIsGreaterThan(fine);
        return loansWithFine.stream().map(LoansEntity::getClient).distinct().collect(Collectors.toList());
    }


}



