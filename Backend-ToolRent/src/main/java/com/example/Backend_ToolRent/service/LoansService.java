package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.*;
import com.example.Backend_ToolRent.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LoansService {

    private final LoansRepository loansRepo;
    private final UnitRepository unitRepo;
    private final ClientRepository clientRepo;
    private final WorkerRepository workerRepo;
    private final KardexService kardexService;


    public LoansService(LoansRepository loansRepo, UnitRepository unitRepo, ClientRepository clientRepo, WorkerRepository workerRepo, KardexRepository kardexRepo, KardexService kardexService) {
        this.loansRepo = loansRepo;
        this.unitRepo = unitRepo;
        this.clientRepo = clientRepo;
        this.workerRepo = workerRepo;
        this.kardexService = kardexService;
    }

    public LoansEntity getLoansById(Long id) {
        return loansRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("LoansEntity not found"));
    }

    public List<LoansEntity> getAllLoans() {
        return loansRepo.findAll();
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

    public List<LoansEntity> getActiveBeforeDate(LocalDate date){
        return loansRepo.findByLoanEndBeforeAndActiveTrue(date);
    }

    public List<LoansEntity> getLoansByActive(){
        return loansRepo.findByActive(true);
    }

    public List<LoansEntity> getLoansByInactive(){return loansRepo.findByActive(false);}

    public long countLoansActive(Long clientId){
        return loansRepo.countByClient_UserIdAndActiveTrue(clientId);
    }

    @Transactional
    public LoansEntity createLoan(LoansEntity loansEntity, Long workerId) {
        WorkerEntity worker = workerRepo.findById(workerId).orElseThrow(() -> new EntityNotFoundException("WorkerEntity not found"));
        Long clientId = loansEntity.getClient().getUserId();
        ClientEntity client = clientRepo.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + clientId));

        if(countLoansActive(clientId) >= 5){
            throw new IllegalStateException("The client have 5 loans active.");

        }

        if ("Restringido".equalsIgnoreCase(client.getState())) {
            throw new IllegalStateException("El cliente está restringido y no puede solicitar préstamos.");
        }
        List<LoanUnitEntity> loansEntities = loansEntity.getLoanUnits();
        Set<Long> toolIdsInLoan = new HashSet<>();
        double totalPrice= 0;
        for(LoanUnitEntity uni : loansEntities){
            Long unitId = uni.getUnit().getUnitId();
            UnitEntity unit = unitRepo.findById(unitId).orElseThrow(() -> new EntityNotFoundException("UnitEntity not found"));


            if (!"Disponible".equalsIgnoreCase(unit.getStatus())){
                throw new IllegalStateException("The unit is not disponible");
            }
            if ("Dañado".equalsIgnoreCase(unit.getCondition()) || "Dada de baja".equalsIgnoreCase(unit.getCondition())) {
                throw new IllegalStateException("La unidad " + unit.getUnitId() + " (" + unit.getTool().getToolName() + ") no se puede prestar.");
            }
            Long toolId = unit.getTool().getToolId();
            if(!toolIdsInLoan.add(toolId)){
                throw new IllegalStateException("The tool has already been added.");
            }

            double dailyPrice = unit.getTool().getDailyPrice();
            totalPrice = totalPrice + dailyPrice;


        }
        Long loanDays = ChronoUnit.DAYS.between(loansEntity.getLoanStart(), loansEntity.getLoanEnd()) +1;
        if(loanDays <= 0) {
            throw new IllegalArgumentException("La fecha de término debe ser igual o posterior a la fecha de inicio.");
        }
        totalPrice = totalPrice * loanDays;
        LoansEntity newLoan = new LoansEntity();
        newLoan.setClient(client); // Usa el cliente seguro cargado de la BD
        newLoan.setLoanStart(loansEntity.getLoanStart());
        newLoan.setLoanEnd(loansEntity.getLoanEnd());
        newLoan.setPrice(totalPrice);
        newLoan.setActive(true);
        client.setDebt(client.getDebt()+ totalPrice);
        clientRepo.save(client);

        for(LoanUnitEntity loanUnit : loansEntities){
            UnitEntity unit = unitRepo.findById(loanUnit.getUnit().getUnitId()).orElseThrow(() -> new EntityNotFoundException("UnitEntity not found"));
            unit.setStatus("Prestada");

            LoanUnitEntity newLoanUnit = new LoanUnitEntity();
            newLoanUnit.setUnit(unit);
            newLoanUnit.setLoan(newLoan); // Asocia al nuevo préstamo
            newLoan.getLoanUnits().add(newLoanUnit);
        }

        if(countLoansActive(clientId) + 1 == 5){
            client.setState("Restringido");
            clientRepo.save(client);
        }

        for (LoanUnitEntity loanUnit : newLoan.getLoanUnits()) {
            String comment = "Salida en préstamo Id: " + newLoan.getLoanId() + "para el cliente Id: " + clientId;
            kardexService.registerMovement(loanUnit.getUnit(), "Salida_Prestamo", worker, newLoan, comment);
        }

        return loansRepo.save(newLoan);

    }

    public LoansEntity returnLoan(Long loanId,Long workerId, Map<Long, String> unitCondition) {

        WorkerEntity worker = workerRepo.findById(workerId).orElseThrow(() -> new EntityNotFoundException("WorkerEntity not found"));
        LoansEntity loan = getLoansById(loanId);

        if(!loan.isActive()){
            throw new IllegalStateException("The loan is not active");
        }

        ClientEntity client = loan.getClient();
        StoreEntity store = loan.getStore();

        double currentDebt = client.getDebt();
        double loanPrice = loan.getPrice();

        //If a fine is needed
        LocalDate returnDate = LocalDate.now();
        LocalDate dueDate = loan.getLoanEnd();
        double totalFine = 0;

        if (returnDate.isAfter(dueDate)) {
            long dayslate = ChronoUnit.DAYS.between(dueDate, returnDate);

            double dailyFine = store.getDailyFine();
            totalFine = dayslate*dailyFine;
            currentDebt = currentDebt + dailyFine;

        }
        client.setDebt(currentDebt);
        clientRepo.save(client);

        loan.setActive(false);

        for (LoanUnitEntity loanUnit : loan.getLoanUnits()) {
            UnitEntity unit = loanUnit.getUnit();
            String condition = unitCondition.get(unit.getUnitId());

            if (condition == null || condition.trim().isEmpty()) {
                throw new IllegalArgumentException("Falta la condición para la unidad con ID: " + unit.getUnitId());
            }

            unit.setStatus("Disponible");
            unit.setCondition(condition);
            loanUnit.setReturnDate(returnDate);

        }
        if ("Restringido".equalsIgnoreCase(client.getState())) {
            if (loansRepo.countByClient_UserIdAndActiveTrue(client.getUserId()) - 1 < 5) {
                client.setState("Activo");
            }
        }

        for (LoanUnitEntity loanUnit : loan.getLoanUnits()) {
            String newCondition = unitCondition.get(loanUnit.getUnit().getUnitId());
            String comment = "Devolución del préstamo ID: " + loan.getLoanId() + ". Condición de entrega: " + newCondition;

            kardexService.registerMovement(
                    loanUnit.getUnit(),
                    "ENTRADA_DEVOLUCION",
                    worker,
                    loan,
                    comment
            );
        }

        return loansRepo.save(loan);
        
    }





}

