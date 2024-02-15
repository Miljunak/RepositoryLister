package com.example.repositorylister.controller;

import com.example.repositorylister.exceptions.ApiRateLimitExceededException;
import com.example.repositorylister.exceptions.UserNotFoundException;
import com.example.repositorylister.service.GitHubService;
import com.example.repositorylister.model.RepositoryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserRepositoryController {

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/{username}/repositories")
    public ResponseEntity<?> getUserRepositories(@PathVariable String username) {
        try {
            List<RepositoryInfo> repositories = gitHubService.getUserRepositories(username);
            return ResponseEntity.ok(repositories);
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "message", e.getMessage()
                    ));
        } catch (ApiRateLimitExceededException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", 403,
                            "message", e.getMessage()
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", 500,
                            "message", "Internal Server Error: " + e.getMessage()
                    ));
        }
    }

}
