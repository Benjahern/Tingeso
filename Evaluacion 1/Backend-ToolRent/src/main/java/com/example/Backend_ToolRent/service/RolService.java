package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.RolEntity;
import com.example.Backend_ToolRent.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Transactional
    public RolEntity createRol(RolEntity rol) {
        rol.setRolName(rol.getRolName().toUpperCase());

        Optional<RolEntity> existingRol = rolRepository.findByRolName(rol.getRolName());
        if (existingRol.isPresent()) {
            throw new IllegalStateException("El rol '" + rol.getRolName() + "' ya existe.");
        }
        return rolRepository.save(rol);
    }

    public RolEntity getRolById(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));
    }

    public RolEntity getRolByName(String name) {
        return rolRepository.findByRolName(name.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con nombre: " + name));
    }

    public List<RolEntity> getAllRoles() {
        return rolRepository.findAll();
    }

    @Transactional
    public void deleteRol(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar: Rol no encontrado con ID: " + id);
        }
        rolRepository.deleteById(id);
    }
}