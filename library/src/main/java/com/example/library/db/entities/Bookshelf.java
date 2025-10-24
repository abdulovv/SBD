package com.example.library.db.entities;

import lombok.Data;

@Data
public class Bookshelf {
    private Integer shelf_id;
    private String name;
    private Author author;


    public Bookshelf(Integer shelf_id, String name, Author author) {
        this.shelf_id = shelf_id;
        this.name = name;
        this.author = author;
    }

    public Bookshelf() {
    }
}



