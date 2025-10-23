package com.example.library;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.repository.AuthorRepository;

import java.sql.SQLException;

public class TESTCLASS {
    public static void main(String[] args) throws SQLException {
        DBManager.createDBIfNotExist();
        AuthorRepository authorRepository = new AuthorRepository();
    }
}
