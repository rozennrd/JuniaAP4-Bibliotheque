package org.javacours.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    private String id;
    private String login;
    private String password;
    private UserRole role;

}
