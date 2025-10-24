package com.example.library.db.entities;

import lombok.Data;

@Data
public class Author {
    private Integer author_id;
    private String first_name;
    private String last_name;
    private String country;

    public Author(Integer author_id, String country, String last_name, String first_name) {
        this.author_id = author_id;
        this.country = country;
        this.last_name = last_name;
        this.first_name = first_name;
    }

    public Author() {
    }

    public Author(String first_name, String last_name, String country) {
        this.country = country;
        this.first_name = first_name;
        this.last_name = last_name;
    }
}
