package Repository;

import Entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Long> {

    List<UnitEntity> findByTool_ToolId(Long toolId);

    List<UnitEntity> findByStatus(String status);

    List<UnitEntity> findByCondition(String condition);

    List<UnitEntity> findByTool_ToolNameContainingIgnoreCase(String toolName);

    Optional<UnitEntity> findFirstByTool_ToolIdAndStatusAndConditionNot(
            Long toolId, String status, String condition);
}
