package com.example.repositorylister.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Repository {
    private long id;
    private String name;
    private boolean fork;
    private Owner owner;

    @Data
    @NoArgsConstructor
    public static class Owner {
        private String login;
    }
}
