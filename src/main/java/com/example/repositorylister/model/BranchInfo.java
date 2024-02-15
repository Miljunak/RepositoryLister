package com.example.repositorylister.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BranchInfo {
    private String name;
    private String lastCommitSha;
}
