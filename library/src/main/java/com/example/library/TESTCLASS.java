package com.example.library;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.db.entities.Genre;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.GenreRepository;

import java.sql.SQLException;

public class TESTCLASS {
    public static void main(String[] args) throws SQLException {
        DBManager.createDBIfNotExist();
        GenreRepository genreRepository = new GenreRepository();

    }
}
