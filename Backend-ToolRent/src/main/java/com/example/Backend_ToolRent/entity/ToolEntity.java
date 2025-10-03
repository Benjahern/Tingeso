package com.example.Backend_ToolRent.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * general class of tools
 */
@Data
@Entity
@Table(name = "tools")
public class ToolEntity {

    /**
     * Id of the tool
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toolId", nullable = false, unique = true)
    private long toolId;

    /**
     * Name of the tool
     */
    @Column(name = "toolName", nullable = false, unique = true)
    private String toolName;

    /**
     * Description of the tool
     */
    @Column(name = "description")
    private String description;

    /**
     * Value of the replacement if the tool breaks
     */
    @Column(name = "replacementValue", nullable = false)
    private double replacementValue;

    /**
     * Category of the tool
     * it is necessary to specify
     */
    @Column(name = "category", nullable = false)
    private String category;

    /**
     * stock of the tool in the store
      */
    @Column(name = "stock")
    private int stock;

    /**
     * Daily price of the tool for the loans
     */
    @Column(name = "dailyPrice")
    private double dailyPrice;

    /**
     * Url of the imagen tool
     * To be seen, maybe I'll delete it.
     */
    @Column(name = "imagen_path")
    private String imagePath;

}
