package Entity;
import jakarta.persistence.*;
import lombok.Data;


import java.util.Set;

/**
 * Class for the workers
 */
@Data
@Entity
@Table(name = "worker")
public class WorkerEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workerId", nullable = false, unique = true)
    private Long workerId;

    /**
     * Rol of the user
     */
    @Column(name = "rol", nullable = false)
    private Set<String> rol;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mail", nullable = false)
    private String mail;

    /**
     * Id asociated to keycloak
     */
    @Column(name = "keycloakId")
    private String keycloakId;

    /**
     * Password of the workers, for login
     */
    @Column(name = "password", nullable = false)
    private String password;

}
