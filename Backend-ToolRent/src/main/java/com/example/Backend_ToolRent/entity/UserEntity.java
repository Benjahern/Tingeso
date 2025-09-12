package com.example.Backend_ToolRent.entity;


import jakarta.persistence.*;
import lombok.Data;

/**
 * user class
 */
@Data
@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserEntity {

    /**
     * Id of teh user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false, unique = true)
    private Long userId;

    /**
     * Name of the user
     */
    @Column(name = "name", nullable = false)
    private String userName;

    /**
     * Mail of the user
     */
    @Column(name = "mail", nullable = false)
    private String mail;

}
