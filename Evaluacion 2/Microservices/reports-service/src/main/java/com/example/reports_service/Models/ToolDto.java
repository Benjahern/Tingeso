package com.example.reports_service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolDto {
    private Long toolId;
    private String toolName;
    private String toolDescription;
    private Double replacementValue;
    private String category;
    private Integer stock;
    private Double dailyPrice;
    private String imagePath;
}
