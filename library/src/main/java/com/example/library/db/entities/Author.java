package com.example.library.db.entities;

import lombok.Data;

@Data
public class Author {
    private Integer author_id;
    private String first_name;
    private String last_name;
    private String country;

}
