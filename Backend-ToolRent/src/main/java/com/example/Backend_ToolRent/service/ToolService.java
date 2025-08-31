package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.model.ToolEntity;
import com.example.Backend_ToolRent.repository.ToolRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ToolService {

    private final ToolRepository toolRepo;

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
    public ToolEntity updateTool(ToolEntity toolEntity) {
        return toolRepo.save(toolEntity);
    }

    public ToolEntity getToolByName(String toolName) {
        return toolRepo.findByToolNameContainingIgnoreCase(toolName).orElseThrow(()->new EntityNotFoundException("Tool not found"));
    }

    @Transactional
    public ToolEntity setDailyPrice(Long id, Double dailyPrice) {
        ToolEntity toolEntity = getToolById(id);
        toolEntity.setDailyPrice(dailyPrice);
        return updateTool(toolEntity);
    }

    @Transactional
    public ToolEntity setReplacementValue(Long id, Double replacementValue) {
        ToolEntity toolEntity = getToolById(id);
        toolEntity.setReplacementValue(replacementValue);
        return updateTool(toolEntity);
    }



}
