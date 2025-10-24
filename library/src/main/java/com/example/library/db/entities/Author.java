package com.example.library.db.entities;

import lombok.Data;

@Data
public class Author {
    private Integer author_id;
    private Country country;
    private String first_name;
    private String last_name;

    public Author(Country country, String first_name, String last_name) {
        this.country = country;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public Author() {

    }
}
