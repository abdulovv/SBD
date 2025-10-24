package com.example.library.db.entities;

import lombok.Data;

@Data
public class User {
    private Integer user_id;
    private PersonalData personalData;
    private String login;
    private String password;
    private Boolean isAdmin;
}
