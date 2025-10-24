package com.example.library.db.entities;

import lombok.Data;

@Data
public class Book {
    private Integer book_id;
    private Author author;
    private Genre genre;
    private Bookshelf bookshelf;
    private String name;
    private Integer year;
}
