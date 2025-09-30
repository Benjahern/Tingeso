package com.example.Backend_ToolRent.controller;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.service.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de Roles.
 * Todas las operaciones en este controlador requieren privilegios de Administrador.
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /**
     * Obtiene una lista de todos los roles del sistema.
     * GET /api/roles
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RolEntity>> getAllRoles() {
        List<RolEntity> roles = rolService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * Obtiene un rol específico por su ID.
     * GET /api/roles/1
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolEntity> getRolById(@PathVariable Long id) {
        RolEntity rol = rolService.getRolById(id);
        return ResponseEntity.ok(rol);
    }

    /**
     * Obtiene un rol específico por su nombre.
     * GET /api/roles/by-name?name=ADMIN
     */
    @GetMapping("/by-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolEntity> getRolByName(@RequestParam String name) {
        RolEntity rol = rolService.getRolByName(name);
        return ResponseEntity.ok(rol);
    }

    /**
     * Crea un nuevo rol en el sistema.
     * POST /api/roles
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolEntity> createRol(@RequestBody RolEntity rol) {
        RolEntity newRol = rolService.createRol(rol);
        // Devuelve un estado 201 Created, que es el estándar para la creación de recursos.
        return ResponseEntity.status(HttpStatus.CREATED).body(newRol);
    }

    /**
     * Elimina un rol del sistema por su ID.
     * DELETE /api/roles/1
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        rolService.deleteRol(id);
        // Devuelve un estado 204 No Content, estándar para eliminaciones exitosas.
        return ResponseEntity.noContent().build();
    }
}
