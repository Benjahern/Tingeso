package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.ToolEntity;
import com.example.Backend_ToolRent.service.ToolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
@CrossOrigin("*")
public class ToolController {

    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<ToolEntity>> getAllTools() {
        List<ToolEntity> tools = toolService.getAllTool();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ToolEntity> getToolById(@PathVariable Long id) {
        ToolEntity tool = toolService.getToolById(id);
        return ResponseEntity.ok(tool);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ToolEntity> searchToolByName(@RequestParam String name) {
        ToolEntity tool = toolService.getToolByName(name);
        return ResponseEntity.ok(tool);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToolEntity> createTool(@RequestBody ToolEntity tool) {
        ToolEntity newTool = toolService.saveTool(tool);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTool);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToolEntity> updateTool(@PathVariable Long id, @RequestBody ToolEntity toolDetails) {
        ToolEntity updatedTool = toolService.updateTool(id, toolDetails);
        return ResponseEntity.ok(updatedTool);
    }

    @PutMapping("/{id}/daily-price")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToolEntity> setDailyPrice(@PathVariable Long id, @RequestParam Double price) {
        ToolEntity updatedTool = toolService.setDailyPrice(id, price);
        return ResponseEntity.ok(updatedTool);
    }

    @PutMapping("/{id}/replacement-value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToolEntity> setReplacementValue(@PathVariable Long id, @RequestParam Double value) {
        ToolEntity updatedTool = toolService.setReplacementValue(id, value);
        return ResponseEntity.ok(updatedTool);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTool(@PathVariable Long id) {
        toolService.deleteToolById(id);
        return ResponseEntity.noContent().build();
    }
}
