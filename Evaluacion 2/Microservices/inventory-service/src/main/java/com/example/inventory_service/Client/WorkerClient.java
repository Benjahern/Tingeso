package com.example.inventory_service.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.inventory_service.Models.WorkerDto;

@FeignClient(name = "users-service")
public interface WorkerClient {

    @GetMapping("/workers/{id}")
    WorkerDto getWorker(@PathVariable("id") Long id);

    @GetMapping("/workers/mail")
    WorkerDto getWorkerByMail(@RequestParam("mail") String mail);
}
