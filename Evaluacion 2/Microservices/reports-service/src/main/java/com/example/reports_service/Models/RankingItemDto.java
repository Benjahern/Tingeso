package com.example.reports_service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingItemDto {
    private Long unitId;
    private String toolName;
    private String category;
    private Long totalSolicitudes;
}
