package com.example.repositorylister.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Repository {
    private long id;
    private String name;
    private boolean fork;
    private Owner owner;

    @Getter
    public static class Owner {
        private String login;
    }
}
