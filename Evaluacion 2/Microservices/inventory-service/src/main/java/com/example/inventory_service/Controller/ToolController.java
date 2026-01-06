package com.example.inventory_service.Controller;

import com.example.inventory_service.Entity.ToolEntity;
import com.example.inventory_service.Service.ToolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tools")
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
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ToolEntity> createTool(@RequestBody ToolEntity tool) {
        System.out.println("Creating tool: " + tool.getToolName() + " with stock: " + tool.getStock());
        ToolEntity newTool = toolService.saveTool(tool);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTool);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ToolEntity> updateTool(@PathVariable Long id, @RequestBody ToolEntity toolDetails) {
        System.out.println("ToolController: Received update request for ID " + id + ": " + toolDetails);
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

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<ToolEntity> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        ToolEntity updatedTool = toolService.uploadImage(id, file);
        return ResponseEntity.ok(updatedTool);
    }

}
