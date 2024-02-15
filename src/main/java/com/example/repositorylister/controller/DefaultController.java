package com.example.repositorylister.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class DefaultController {

    @CrossOrigin(origins = "*")
    @GetMapping("/**")
    public ResponseEntity<?> getDefaultResponse() {
        String message = "Welcome to the RepositoryLister. Use /api/users/{username}/repositories to list non-fork repositories.";
        return ResponseEntity.ok(Map.of("message", message));
    }
}
