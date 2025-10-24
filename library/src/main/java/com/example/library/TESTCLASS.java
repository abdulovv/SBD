package com.example.library;

import com.example.library.db.DBManager;
import com.example.library.db.entities.*;
import com.example.library.repository.*;

import java.sql.SQLException;

public class TESTCLASS {
    public static void main(String[] args) throws SQLException {
        DBManager.createDBIfNotExist();


    }
}
