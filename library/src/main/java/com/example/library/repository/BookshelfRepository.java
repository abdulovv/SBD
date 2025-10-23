package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.db.entities.Bookshelf;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Repository
public class BookshelfRepository {

    //СМОТРИ МЕТОД ПОИСКА ПО ID В BOOKREPOSITORY, Я ВСЕ РАСПИСАЛ
    public Bookshelf findBookshelfById(Integer bookshelfId) throws SQLException {
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Bookshelf WHERE shelf_id = ?");
        preparedStatement.setInt(1, bookshelfId);
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();

        if (resultSet.next()) {
            Bookshelf bookshelf = new Bookshelf();
            bookshelf.setName(resultSet.getString("name"));
            bookshelf.setShelf_id(resultSet.getInt("shelf_id"));

            Author author = new Author();


            return bookshelf;
        }
        return null;
    }
}
