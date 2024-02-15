package com.example.repositorylister.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BranchInfo {
    private String name;
    private String lastCommitSha;
}
