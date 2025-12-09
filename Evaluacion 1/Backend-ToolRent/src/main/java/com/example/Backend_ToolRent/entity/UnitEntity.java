package com.example.Backend_ToolRent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Class of tools per unit
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "unit")
@ToString(exclude = "loanUnits")
public class UnitEntity {

    /**
     * Id of the unit
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unitId", nullable = false, unique = true)
    private Long unitId;

    /**
     * Relation de unit with the tool
     */
    @ManyToOne(fetch = FetchType.EAGER) // lazy es para que no lo cargue siempre, solo cuando sea solicitado
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

    /**
     * loan history
     */
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("loanunit-unit")
    private List<LoanUnitEntity> loanUnits;

}
