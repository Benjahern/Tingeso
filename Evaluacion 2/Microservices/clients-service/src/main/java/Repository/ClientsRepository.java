package Repository;

import Entity.ClientsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientsRepository extends JpaRepository<ClientsEntity, Long> {


    List<ClientsEntity> findByNameContainingIgnoreCase(String userName);

    List<ClientsEntity> findByDebtGreaterThan(double debt);

    List<ClientsEntity> findByState(String state);

    List<ClientsEntity> findByRut(String rut);

}
