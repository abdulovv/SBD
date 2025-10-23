package com.example.library.db.entities;

import lombok.Data;

@Data
public class Log {
    private Integer log_id;
    private User user;
    private String description;
}
