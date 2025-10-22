package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Book;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Repository
public class BookRepository {
    public Book findBookById(int id) throws SQLException {
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        DBManager.closeConnection();
        return resultSet.next() ? new Book() : null;
    }
}

