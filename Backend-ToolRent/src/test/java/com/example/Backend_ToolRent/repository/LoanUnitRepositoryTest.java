package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LoanUnitRepositoryTest {

    @Autowired
    private LoanUnitRepository loanUnitRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UnitEntity unit;

    @BeforeEach
    void setUp() {
        // --- 1. Crear dependencias base (Tool, Store, Client) ---

        // 1.1 ToolEntity
        ToolEntity tool = new ToolEntity();
        tool.setToolName("Martillo");
        tool.setCategory("Herramienta Manual");
        tool.setReplacementValue(50.0);
        entityManager.persist(tool);

        // 1.2 StoreEntity
        StoreEntity store = new StoreEntity();
        store.setDailyFine(100);
        entityManager.persist(store);

        // 1.3 ClientEntity
        ClientEntity client = new ClientEntity();
        client.setRut("12345678-9");
        client.setPhone("987654321");
        client.setMail("client@example.com");
        client.setName("John Doe");
        entityManager.persist(client);

        // 1.4 Unit
        // 🛠️ CORRECCIÓN CLAVE: NO USAR 'UnitEntity' aquí. Inicializamos la variable de clase 'this.unit'.
        unit = new UnitEntity();
        unit.setTool(tool);
        unit.setStatus("Prestado");
        unit.setCondition("Bueno");
        entityManager.persist(unit); // Persistimos la unidad


        // --- 2. Crear LoansEntity ---
        LoansEntity loan = new LoansEntity();
        loan.setClient(client);
        loan.setStore(store);

        loan.setActive(true);
        loan.setLoanStart(LocalDate.now());
        loan.setLoanEnd(LocalDate.now().plusDays(7));
        loan.setPrice(5.50F);

        entityManager.persist(loan);
        // --- 3. Crear LoanUnit ---
        LoanUnitEntity loanUnit1 = new LoanUnitEntity();
        loanUnit1.setUnit(unit);
        loanUnit1.setLoan(loan);
        loanUnit1.setState("Entregado");
        entityManager.persist(loanUnit1);

        LoanUnitEntity loanUnit2 = new LoanUnitEntity();
        loanUnit2.setUnit(unit);
        loanUnit2.setLoan(loan);
        loanUnit2.setState("Pendiente");
        entityManager.persist(loanUnit2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar historial de préstamos por ID de Unidad")
    void findByUnit_UnitId() {
        List<LoanUnitEntity> history = loanUnitRepository.findByUnit_UnitId(unit.getUnitId());

        assertThat(history).hasSize(2);
        assertThat(history).extracting(LoanUnitEntity::getUnit).containsOnly(unit);
    }

    @Test
    @DisplayName("Debe encontrar devoluciones por Estado")
    void findByState() {
        List<LoanUnitEntity> pending = loanUnitRepository.findByState("Pendiente");

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getState()).isEqualTo("Pendiente");
    }
}