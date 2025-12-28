package Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "units")
public class UnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unitId", unique = true, nullable = false)
    private Long unitId;

    @ManyToOne
    @JoinColumn(name = "toolId", nullable = false)
    private ToolEntity tool;

    /**
     * State of the tool unit
     * "Disponible" if the tool is available
     * "Prestada" if the tool is in loan
     * "En reparación" if the tool is in reparation
     * "Dada de baja" if the tool is broken and have no fix
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * state of teh tool like is in good state or bad or broken
     */
    @Column(name = "condition", nullable = false)
    private String condition;
}
