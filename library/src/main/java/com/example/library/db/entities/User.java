package com.example.library.db.entities;

import lombok.Data;

@Data
public class User {
    private Integer user_id;
    private String login;
    private String password;
    private String first_name;
    private String last_name;
    private Boolean isAdmin;
}
