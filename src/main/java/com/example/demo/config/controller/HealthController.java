package com.example.demo.config.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "API REST backend petromil corriendo");
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}
