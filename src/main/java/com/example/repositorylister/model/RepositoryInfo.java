package com.example.repositorylister.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RepositoryInfo {

    private String name;
    private String ownerLogin;
    private List<BranchInfo> branches;
}
