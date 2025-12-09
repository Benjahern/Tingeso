package com.example.Backend_ToolRent.service;

// ASEGÚRATE DE QUE TUS IMPORTS SEAN EXACTAMENTE ESTOS PARA EVITAR CONFLICTOS
import com.example.Backend_ToolRent.entity.*;
import com.example.Backend_ToolRent.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any; // Importante: Mockito Matchers
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoansServiceTest {

    @Mock
    private LoansRepository loansRepo;
    @Mock
    private UnitRepository unitRepo;
    @Mock
    private ClientRepository clientRepo;
    @Mock
    private WorkerRepository workerRepo;
    @Mock
    private KardexService kardexService;
    @Mock
    private StoreRepository storeRepo;
    @Mock
    private ToolRepository toolRepo;
    @Mock
    private UnitService unitService;
    @Mock
    private WorkerService workerService;
    @Mock
    private ToolService toolService;
    @Mock
    private KardexRepository kardexRepo;

    @InjectMocks
    private LoansService loansService;

    private LoansEntity buildLoan() {
        LoansEntity loan = new LoansEntity();
        loan.setLoanId(1L);
        loan.setLoanStart(LocalDate.now());
        loan.setLoanEnd(LocalDate.now().plusDays(3));
        loan.setPrice(100.0);
        loan.setActive(true);
        return loan;
    }

    @Test
    @DisplayName("getLoansById devuelve préstamo cuando existe")
    void getLoansById_whenExists_returnsLoan() {
        LoansEntity loan = buildLoan();
        given(loansRepo.findById(1L)).willReturn(Optional.of(loan));

        LoansEntity result = loansService.getLoansById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getLoanId()).isEqualTo(1L);
        verify(loansRepo).findById(1L);
    }

    @Test
    @DisplayName("getLoansById lanza EntityNotFoundException cuando no existe")
    void getLoansById_whenNotExists_throwsException() {
        given(loansRepo.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> loansService.getLoansById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("LoansEntity not found");

        verify(loansRepo).findById(1L);
    }

    @Test
    @DisplayName("getAllLoans delega en loansRepo.findAll")
    void getAllLoans_delegatesToRepository() {
        given(loansRepo.findAll()).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getAllLoans();

        assertThat(result).hasSize(1);
        verify(loansRepo).findAll();
    }

    @Test
    @DisplayName("getLoansByClientId delega en repositorio")
    void getLoansByClientId_delegatesToRepository() {
        given(loansRepo.findByClient_UserId(10L)).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getLoansByClientId(10L);

        assertThat(result).hasSize(1);
        verify(loansRepo).findByClient_UserId(10L);
    }

    @Test
    @DisplayName("getLoansByActive delega en repositorio")
    void getLoansByActive_delegatesToRepository() {
        given(loansRepo.findByActive(true)).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getLoansByActive();

        assertThat(result).hasSize(1);
        verify(loansRepo).findByActive(true);
    }

    @Test
    @DisplayName("getLoansByInactive delega en repositorio")
    void getLoansByInactive_delegatesToRepository() {
        given(loansRepo.findByActive(false)).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getLoansByInactive();

        assertThat(result).hasSize(1);
        verify(loansRepo).findByActive(false);
    }

    @Test
    @DisplayName("countLoansActive delega en countByClient_UserIdAndActiveTrue")
    void countLoansActive_delegatesToRepository() {
        given(loansRepo.countByClient_UserIdAndActiveTrue(5L)).willReturn(3L);

        long result = loansService.countLoansActive(5L);

        assertThat(result).isEqualTo(3L);
        verify(loansRepo).countByClient_UserIdAndActiveTrue(5L);
    }

    @Test
    @DisplayName("getByLoanStart delega en repositorio")
    void getByLoanStart_delegatesToRepository() {
        LocalDate date = LocalDate.now();
        given(loansRepo.findByLoanStart(date)).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getByLoanStart(date);

        assertThat(result).hasSize(1);
        verify(loansRepo).findByLoanStart(date);
    }

    @Test
    @DisplayName("getByLoanEnd delega en repositorio")
    void getByLoanEnd_delegatesToRepository() {
        LocalDate date = LocalDate.now();
        given(loansRepo.findByLoanEnd(date)).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getByLoanEnd(date);

        assertThat(result).hasSize(1);
        verify(loansRepo).findByLoanEnd(date);
    }

    @Test
    @DisplayName("getActiveBeforeDate delega en repositorio")
    void getActiveBeforeDate_delegatesToRepository() {
        LocalDate date = LocalDate.now();
        given(loansRepo.findByLoanEndBeforeAndActiveTrue(date)).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getActiveBeforeDate(date);

        assertThat(result).hasSize(1);
        verify(loansRepo).findByLoanEndBeforeAndActiveTrue(date);
    }

    @Test
    @DisplayName("getClientsWithFine delega en findByFineIsGreaterThan")
    void getClientsWithFine_delegatesToRepository() {
        given(loansRepo.findByFineIsGreaterThan(0L)).willReturn(List.of(buildLoan()));

        List<?> result = loansService.getClientsWithFine(0L);

        assertThat(result).hasSize(1);
        verify(loansRepo).findByFineIsGreaterThan(0L);
    }

    @Test
    @DisplayName("createLoan crea préstamo exitosamente cuando todo es válido")
    void createLoan_success() {
        // 1. Preparar datos de prueba
        Long clientId = 1L;
        Long storeId = 1L;
        Long workerId = 1L;
        List<Long> toolIds = List.of(100L);
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);

        ClientEntity client = new ClientEntity();
        client.setUserId(clientId);
        client.setState("ACTIVO");
        client.setDebt(0);

        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(workerId);

        StoreEntity store = new StoreEntity();
        store.setStoreId(storeId);

        ToolEntity tool = new ToolEntity();
        tool.setToolId(100L);
        tool.setToolName("Taladro");
        tool.setDailyPrice(1000.0);
        tool.setStock(5);

        UnitEntity unit = new UnitEntity();
        unit.setUnitId(1L);
        unit.setStatus("Disponible");
        unit.setCondition("Bueno");
        unit.setTool(tool);

        // 2. Mockear repositorios y comportamientos
        given(workerRepo.findById(workerId)).willReturn(Optional.of(worker));
        given(clientRepo.findById(clientId)).willReturn(Optional.of(client));
        given(loansRepo.countByClient_UserIdAndActiveTrue(clientId)).willReturn(0L);
        // No hay préstamos activos previos, así que no valida duplicados ni vencidos
        given(loansRepo.findByClient_UserIdAndActiveTrue(clientId)).willReturn(Collections.emptyList());
        given(storeRepo.findById(storeId)).willReturn(Optional.of(store));
        given(unitRepo.findFirstByTool_ToolIdAndStatusAndConditionNot(eq(100L), eq("Disponible"), eq("Dañado")))
                .willReturn(Optional.of(unit));
        given(toolRepo.findById(100L)).willReturn(Optional.of(tool));
        given(loansRepo.save(any(LoansEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // 3. Ejecutar el método
        LoansEntity result = loansService.createLoan(clientId, storeId, start, end, toolIds, workerId);

        // 4. Verificaciones
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(3000.0); // 1000 * 3 días (inclusive start/end)
        assertThat(result.isActive()).isTrue();

        // Verificar que se redujo el stock
        assertThat(tool.getStock()).isEqualTo(4);
        // Verificar que la unidad cambió de estado
        verify(unitRepo, atLeastOnce()).save(unit); // Se guarda al cambiar estado
        // Verificar registro en Kardex
        verify(kardexService).registerMovement(any(), eq("Salida_Prestamo"), eq(worker), any(), anyString());
    }

    @Test
    @DisplayName("createLoan lanza excepción si el cliente está restringido")
    void createLoan_throwsWhenClientRestricted() {
        ClientEntity client = new ClientEntity();
        client.setState("RESTRINGIDO");

        given(workerRepo.findById(1L)).willReturn(Optional.of(new WorkerEntity()));
        given(clientRepo.findById(1L)).willReturn(Optional.of(client));
        given(loansRepo.countByClient_UserIdAndActiveTrue(1L)).willReturn(0L);

        assertThatThrownBy(() -> loansService.createLoan(1L, 1L, LocalDate.now(), LocalDate.now(), List.of(1L), 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("restringido");
    }

    @Test
    @DisplayName("createLoan lanza excepción si hay herramienta duplicada en préstamo activo")
    void createLoan_throwsWhenDuplicateTool() {
        Long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setState("ACTIVO");

        // Simular un préstamo activo que ya tiene la herramienta ID 100
        ToolEntity tool = new ToolEntity();
        tool.setToolId(100L);
        tool.setToolName("Sierra");

        UnitEntity unit = new UnitEntity();
        unit.setTool(tool);

        LoanUnitEntity loanUnit = new LoanUnitEntity();
        loanUnit.setUnit(unit);

        LoansEntity activeLoan = new LoansEntity();
        activeLoan.setLoanEnd(LocalDate.now().plusDays(5)); // No vencido
        activeLoan.setLoanUnits(List.of(loanUnit));

        given(workerRepo.findById(1L)).willReturn(Optional.of(new WorkerEntity()));
        given(clientRepo.findById(clientId)).willReturn(Optional.of(client));
        given(loansRepo.countByClient_UserIdAndActiveTrue(clientId)).willReturn(1L);
        given(loansRepo.findByClient_UserIdAndActiveTrue(clientId)).willReturn(List.of(activeLoan));
        given(toolRepo.findById(100L)).willReturn(Optional.of(tool));

        assertThatThrownBy(() -> loansService.createLoan(clientId, 1L, LocalDate.now(), LocalDate.now(), List.of(100L), 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya tiene préstamos activos");
    }

    @Test
    @DisplayName("returnLoan procesa devolución correctamente sin multas ni daños")
    void returnLoan_success() {
        // 1. SETUP DE DATOS
        Long loanId = 1L;
        Long workerId = 1L;

        // Crear entidades con datos mínimos necesarios para evitar NullPointerExceptions
        ToolEntity tool = new ToolEntity();
        tool.setStock(10);
        tool.setDailyPrice(100.0);

        UnitEntity unit = new UnitEntity();
        unit.setUnitId(50L);
        unit.setTool(tool);
        unit.setStatus("Prestada");

        LoanUnitEntity loanUnit = new LoanUnitEntity();
        loanUnit.setUnit(unit);
        loanUnit.setLoan(null); // Romper ciclo

        ClientEntity client = new ClientEntity();
        client.setUserId(10L);
        client.setDebt(1000);
        client.setState("ACTIVO");

        LoansEntity loan = new LoansEntity();
        loan.setLoanId(loanId);
        loan.setActive(true);
        loan.setClient(client);
        loan.setStore(new StoreEntity()); // Store vacía para evitar nulls
        loan.setLoanEnd(LocalDate.now().plusDays(5)); // Fecha futura = sin multa
        loan.setLoanUnits(List.of(loanUnit));
        loan.setPrice(5000);

        WorkerEntity worker = new WorkerEntity();
        worker.setUserId(workerId);

        // 2. MOCKING CON ESTRATEGIA 'ANSWER' (Más robusta)
        given(workerRepo.findById(workerId)).willReturn(Optional.of(worker));
        given(loansRepo.findById(loanId)).willReturn(Optional.of(loan));

        // Mocks 'lenient' para repositorios secundarios (por si acaso se llaman)
        lenient().when(clientRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        lenient().when(unitRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        // SOLUCIÓN DEFINITIVA PARA EL ERROR:
        // Usamos Mockito.any() explícito y thenAnswer.
        // Esto intercepta CUALQUIER llamada a save() y devuelve inmediatamente el objeto que se le pasó.
        // Funciona incluso si el objeto ha mutado por completo.
        org.mockito.Mockito.when(loansRepo.save(org.mockito.ArgumentMatchers.any(LoansEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 3. EJECUCIÓN
        Map<Long, String> conditions = new HashMap<>();
        conditions.put(50L, "Bueno");

        LoansEntity result = loansService.returnLoan(loanId, workerId, conditions);

        // 4. VERIFICACIONES
        assertThat(result)
                .as("El servicio devolvió null, lo que significa que el mock de loansRepo.save() no funcionó")
                .isNotNull();

        assertThat(result.isActive()).isFalse();
        assertThat(tool.getStock()).isEqualTo(11); // 10 + 1
    }



    @Test
    @DisplayName("returnLoan aplica multa por atraso y ajusta deuda")
    void returnLoan_withFine() {
        Long loanId = 1L;
        LocalDate dueDate = LocalDate.now().minusDays(1); // Vencido ayer

        StoreEntity store = new StoreEntity();
        store.setDailyFine(1000);

        ClientEntity client = new ClientEntity();
        client.setUserId(5L);
        client.setDebt(0); // Deuda inicial 0
        client.setState("ACTIVO");

        LoansEntity loan = new LoansEntity();
        loan.setLoanId(loanId);
        loan.setActive(true);
        loan.setLoanEnd(dueDate);
        loan.setStore(store);
        loan.setClient(client);
        loan.setPrice(2000);
        loan.setLoanUnits(Collections.emptyList());

        given(workerRepo.findById(1L)).willReturn(Optional.of(new WorkerEntity()));
        given(loansRepo.findById(loanId)).willReturn(Optional.of(loan));

        // SOLUCIÓN CLAVE: Usar doAnswer
        doAnswer(invocation -> invocation.getArgument(0))
                .when(loansRepo).save(any(LoansEntity.class));

        // Ejecución
        LoansEntity result = loansService.returnLoan(loanId, 1L, Collections.emptyMap());

        // Verificaciones
        // Multa: 1 día atraso * 1000 = 1000
        assertThat(result.getFine()).isEqualTo(1000L);

        // Validación de Deuda:
        // Lógica del servicio:
        // 1. Suma multa: deuda = 0 + 1000 = 1000
        // 2. Resta multa y precio: deuda = 1000 - 1000 - 2000 = -2000
        // Verificamos el valor final exacto:
        assertThat(client.getDebt()).isEqualTo(-2000.0);
    }



    @Test
    @DisplayName("removeLoansById restaura stock y elimina kardex")
    void removeLoansById_success() {
        Long loanId = 1L;

        ToolEntity tool = new ToolEntity();
        tool.setStock(5);

        UnitEntity unit = new UnitEntity();
        unit.setTool(tool);

        LoanUnitEntity loanUnit = new LoanUnitEntity();
        loanUnit.setUnit(unit);

        LoansEntity loan = new LoansEntity();
        loan.setLoanId(loanId);
        loan.setLoanUnits(List.of(loanUnit));

        given(loansRepo.findById(loanId)).willReturn(Optional.of(loan));

        // Ejecución
        loansService.removeLoansById(loanId);

        // Verificación
        // Stock debe aumentar
        assertThat(tool.getStock()).isEqualTo(6);
        verify(unitRepo).save(unit);
        verify(toolRepo).save(tool);
        verify(kardexRepo).deleteByLoan_LoanId(loanId);
        verify(loansRepo).delete(loan);
    }

    @Test
    @DisplayName("Métodos de búsqueda por String delegan correctamente")
    void searchMethods_delegateToRepo() {
        // getLoansByClientName
        loansService.getLoansByClientName("Juan");
        verify(loansRepo).findByClient_NameContainingIgnoreCase("Juan");

        // getLoansByClientRut
        loansService.getLoansByClientRut("12345678-9");
        verify(loansRepo).findByClient_RutContainingIgnoreCase("12345678-9");

        // getActiveLoansByClientRut
        loansService.getActiveLoansByClientRut("12345678-9");
        verify(loansRepo).findByClient_RutAndActiveTrue("12345678-9");
    }

    @Test
    @DisplayName("checkClient aplica multas y restringe clientes atrasados")
    void checkClient_appliesFines() {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.minusDays(2); // 2 días atrasado

        StoreEntity store = new StoreEntity();
        store.setDailyFine(500);

        ClientEntity client = new ClientEntity();
        client.setState("ACTIVO");

        LoansEntity lateLoan = new LoansEntity();
        lateLoan.setLoanEnd(dueDate);
        lateLoan.setActive(true);
        lateLoan.setStore(store);
        lateLoan.setClient(client);

        // El repo devuelve préstamos que vencen antes de "hoy"
        given(loansRepo.findByLoanEndBeforeAndActiveTrue(today)).willReturn(List.of(lateLoan));

        // Ejecución
        loansService.checkClient();

        // Verificación
        // 2 días * 500 = 1000
        assertThat(lateLoan.getFine()).isEqualTo(1000L);
        // Cliente debe pasar a restringido
        assertThat(client.getState()).isEqualTo("Restringido");

        verify(loansRepo).save(lateLoan);
        verify(clientRepo).save(client);
    }





}
