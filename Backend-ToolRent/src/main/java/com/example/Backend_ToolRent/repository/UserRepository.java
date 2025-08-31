package com.example.Backend_ToolRent.repository;

import com.example.Backend_ToolRent.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserNameContainingIgnoreCase(String userName);

    Optional<UserEntity> findByMail(String mail);


}
