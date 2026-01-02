package com.example.reports_service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDto {
    private Long unitId;
    private ToolDto tool;
    private String status;
    private String condition;
}
