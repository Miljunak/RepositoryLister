package com.example.repositorylister.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Branch {

    private String name;
    private Commit commit;

    @Getter
    public static class Commit {
        private String sha;
    }
}
