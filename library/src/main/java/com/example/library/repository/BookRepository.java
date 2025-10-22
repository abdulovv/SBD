package com.example.library.repository;

import com.example.library.db.DBManager;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class BookRepository {
    private DBManager dbManager;

    @Autowired
    public BookRepository(DBManager dbManager){
        this.dbManager = dbManager;
    }

}

