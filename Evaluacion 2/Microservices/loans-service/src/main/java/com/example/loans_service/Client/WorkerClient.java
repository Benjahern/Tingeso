package com.example.loans_service.Client;

import com.example.loans_service.Models.WorkerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service")
public interface WorkerClient {

    @GetMapping("/workers/{id}")
    WorkerDto getWorker(@PathVariable("id") Long id);

    @GetMapping("/workers/mail")
    WorkerDto getWorkerByMail(@RequestParam("mail") String mail);
}
