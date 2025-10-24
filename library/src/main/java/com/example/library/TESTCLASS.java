package com.example.library;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Bookshelf;
import com.example.library.db.entities.PersonalData;
import com.example.library.db.entities.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BookshelfRepository;
import com.example.library.repository.UserRepository;

import java.sql.SQLException;

public class TESTCLASS {
    public static void main(String[] args) throws SQLException {
        DBManager.createDBIfNotExist();



    }
}
