package com.example.loans_service.Client;

import com.example.loans_service.Models.ToolDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", contextId = "toolClient")
public interface ToolClient {

    @PutMapping("/tools/{id}")
    ToolDto updateTool(@PathVariable("id") Long id, @RequestBody ToolDto toolDto);

    @GetMapping("/tools/{id}")
    ToolDto getTool(@PathVariable("id") Long id);

}
