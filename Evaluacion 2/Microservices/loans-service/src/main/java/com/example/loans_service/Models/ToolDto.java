package com.example.loans_service.Models;

import lombok.Data;

@Data
public class ToolDto {

    private long toolId;

    private String toolName;

    private String toolDescription;

    private Double replacementValue;

    private String category;

    private int stock;

    private Double dailyPrice;

    private String imagePath;
}
