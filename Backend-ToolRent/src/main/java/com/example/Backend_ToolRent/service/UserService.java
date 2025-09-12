package com.example.Backend_ToolRent.service;

import com.example.Backend_ToolRent.entity.UserEntity;
import com.example.Backend_ToolRent.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * Dependency
     */
    private final UserRepository userRepo;

    /**
     * Constructor
     * @param userRepo
     */
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Function to get a user by Id
     * @param id
     * @return
     */
    public UserEntity getUserById(Long id){
        return userRepo.findById(id).orElseThrow(()->new EntityNotFoundException("User Not Found"));
    }

    /**
     * Function to get a user by name
     * @param name
     * @return
     */
    public UserEntity getUserByName(String name){
        return userRepo.findByUserNameContainingIgnoreCase(name).orElseThrow(()->new EntityNotFoundException("User Not Found"));
    }

    /**
     * Functio to get a user by mail
     * @param email
     * @return
     */
    public UserEntity getUserByEmail(String email){
        return userRepo.findByMail(email).orElseThrow(()->new EntityNotFoundException("User Not Found"));
    }

}
