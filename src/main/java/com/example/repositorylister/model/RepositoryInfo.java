package com.example.repositorylister.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class RepositoryInfo {

    private String name;
    private String ownerLogin;
    private List<BranchInfo> branches;
}
