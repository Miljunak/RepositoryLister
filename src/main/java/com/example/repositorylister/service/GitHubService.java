package com.example.repositorylister.service;

import com.example.repositorylister.exceptions.ApiRateLimitExceededException;
import com.example.repositorylister.exceptions.UserNotFoundException;
import com.example.repositorylister.model.Branch;
import com.example.repositorylister.model.BranchInfo;
import com.example.repositorylister.model.Repository;
import com.example.repositorylister.model.RepositoryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryInfo> getUserRepositories(String username) {
        // Build the URL to list the user's repositories
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.github.com/users/{username}/repos")
                .queryParam("type", "owner") // I didn't include member as it would also show forks
                .buildAndExpand(username)
                .toUriString();

        logger.info("Fetching GitHub repositories for username: {}", username);

        HttpEntity<String> entity = initializeEntity(); // Handles if there is token set up

        // Fetch the repositories
        List<Repository> repositories;
        try {
            ResponseEntity<Repository[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Repository[].class);
            repositories = Arrays.asList(Objects.requireNonNull(response.getBody()));
            logger.debug("Successfully fetched {} repositories for username: {}", repositories.size(), username);
            logger.info("Rate limit remaining: {}", response.getHeaders().getFirst("X-RateLimit-Remaining"));

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("User not found: {}", username, e);
                throw new UserNotFoundException("User not found: " + username);

            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                logger.warn("GitHub API rate limit exceeded while requesting for username: {}", username, e);
                throw new ApiRateLimitExceededException("GitHub API rate limit exceeded. Please try again later.");

            } else {
                logger.error("Error fetching user repositories for username: {}", username, e);
                throw new RuntimeException("Error fetching user repositories: " + e.getMessage());
            }
        }

        // Filters out forks and maps to RepositoryInfo
        return repositories.stream()
                .filter(repo -> !repo.isFork())
                .map(repo -> new RepositoryInfo(
                        repo.getName(),
                        repo.getOwner().getLogin(),
                        getBranchesForRepository(repo.getOwner().getLogin(), repo.getName()))
                ).collect(Collectors.toList());
    }

    private List<BranchInfo> getBranchesForRepository(String owner, String repoName) {
        // Build the URL to fetch branches for a repository
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.github.com/repos/{owner}/{repo}/branches")
                .buildAndExpand(owner, repoName)
                .toUriString();

        logger.info("Fetching branches for repository: {}/{}", owner, repoName);

        HttpEntity<String> entity = initializeEntity();

        // Fetch the branches
        List<Branch> branches;
        try {
            ResponseEntity<Branch[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Branch[].class);
            branches = Arrays.asList(Objects.requireNonNull(response.getBody()));
            logger.debug("Successfully fetched {} branches for repository: {}/{}", branches.size(), owner, repoName);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                logger.warn("GitHub API rate limit exceeded while fetching branches for repository: {}/{}", owner, repoName, e);
                throw new ApiRateLimitExceededException("GitHub API rate limit exceeded. Please try again later.");

            } else {
                logger.error("Failed to fetch branches for repository: {}/{}", owner, repoName, e);
                throw new RuntimeException("Failed to fetch branches for repository: " + e.getMessage());
            }
        }

        // Map to BranchInfo
        return branches.stream()
                .map(branch -> new BranchInfo(
                        branch.getName(),
                        branch.getCommit().getSha())
                ).collect(Collectors.toList());
    }

    private HttpEntity<String> initializeEntity() {
        HttpHeaders headers = new HttpHeaders();
        // Only set the Authorization header if githubToken is not empty
        if (!githubToken.isBlank()) {
            headers.set("Authorization", "token " + githubToken);
        }
        return new HttpEntity<>(headers);
    }
}
