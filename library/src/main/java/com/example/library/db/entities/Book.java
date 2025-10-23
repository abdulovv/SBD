package com.example.library.db.entities;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Book {
    private Integer book_id;
    private Author author;
    private Genre genre;
    private Bookshelf bookshelf;
    private Integer year;
    private String name;

}
