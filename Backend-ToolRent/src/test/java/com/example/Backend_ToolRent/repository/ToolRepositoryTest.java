package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.repository.ToolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ToolRepositoryTest {

    @Autowired
    private ToolRepository toolRepository;

    @Test
    @DisplayName("Buscar herramienta por nombre (contiene, case insensitive)")
    void testFindByToolNameContainingIgnoreCase() {
        // Preparar entidad y guardar en bd temporal
        ToolEntity tool = new ToolEntity();
        tool.setToolName("Taladro");
        tool.setDescription("Taladro eléctrico");
        tool.setCategory("Herramienta eléctrica");
        tool.setReplacementValue(100.0);
        tool.setStock(10);
        tool.setDailyPrice(5.0);
        toolRepository.save(tool);

        // Probar método de búsqueda
        Optional<ToolEntity> found = toolRepository.findByToolNameContainingIgnoreCase("taladro");

        // AssertJ aserciones
        assertThat(found).isPresent();
        assertThat(found.get().getToolName()).isEqualTo("Taladro");
    }
}
