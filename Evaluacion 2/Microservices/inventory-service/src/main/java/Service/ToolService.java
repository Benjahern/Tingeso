package Service;

import Entity.ToolEntity;
import Repository.ToolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ToolService {

    public final ToolRepository toolRepo;
    public ToolService(ToolRepository toolRepo) {
        this.toolRepo = toolRepo;
    }

    @Transactional
    public ToolEntity saveTool(ToolEntity toolEntity) {
        return toolRepo.save(toolEntity);
    }

    public ToolEntity getToolById(Long toolId) {
        return toolRepo.findById(toolId).orElseThrow(()->new EntityNotFoundException("Tool not found"));
    }

    public List<ToolEntity> getAllTool() {
        return toolRepo.findAll();
    }

    @Transactional
    public void deleteToolById(Long toolId) {
        toolRepo.deleteById(toolId);
    }

    @Transactional
    public ToolEntity updateTool(Long toolId, ToolEntity toolDetails) {
        ToolEntity toolToUpdate = getToolById(toolId);

        toolToUpdate.setToolName(toolDetails.getToolName());
        toolToUpdate.setToolDescription(toolDetails.getToolDescription());
        toolToUpdate.setCategory(toolDetails.getCategory());

        return toolRepo.save(toolToUpdate);
    }

    public ToolEntity getToolByName(String toolName) {
        return toolRepo.findByToolNameContainingIgnoreCase(toolName).orElseThrow(()->new EntityNotFoundException("Tool not found"));
    }

    @Transactional
    public ToolEntity setDailyPrice(Long id, Double dailyPrice) {
        ToolEntity toolEntity = getToolById(id);
        toolEntity.setDailyPrice(dailyPrice);
        return toolRepo.save(toolEntity);
    }

    @Transactional
    public ToolEntity setReplacementValue(Long id, Double replacementValue) {
        ToolEntity toolEntity = getToolById(id);
        toolEntity.setReplacementValue(replacementValue);
        return toolRepo.save(toolEntity);
    }


}
