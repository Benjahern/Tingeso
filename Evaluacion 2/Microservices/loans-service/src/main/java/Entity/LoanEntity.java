package Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "loans")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @Column(name = "clientId", nullable = false)
    private Long clientId;  // Solo ID, cliente está en otro servicio

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanUnitEntity> loanUnits;  // ✅ Esta lista SÍ se mantiene (mismo servicio)
}