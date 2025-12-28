package Repository;

import Entity.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, Long> {

    Optional<WorkerEntity> findByNameIgnoreCase(String name);

    Optional<WorkerEntity> findByMail(String mail);

}
