package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.model.UserEntity;
import com.example.Backend_ToolRent.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public UserEntity getUserById(Long id){
        return userRepo.findById(id).orElseThrow(()->new EntityNotFoundException("User Not Found"));
    }

    public UserEntity getUserByName(String name){
        return userRepo.findByName(name).orElseThrow(()->new EntityNotFoundException("User Not Found"));
    }

}
