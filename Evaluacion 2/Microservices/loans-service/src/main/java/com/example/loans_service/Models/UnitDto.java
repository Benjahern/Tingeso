package com.example.loans_service.Models;

import lombok.Data;

@Data
public class UnitDto {

    private Long unitId;
    private ToolDto tool;
    private String status;
    private String condition;
}
