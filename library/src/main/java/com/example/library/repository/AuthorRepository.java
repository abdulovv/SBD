package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Repository
public class AuthorRepository {

    //ТУТ ХАЛЯВА, СМОТРИ МЕТОД ПОИСКА ПО ID В BOOKREPOSITORY, Я ВСЕ РАСПИСАЛ
    public Author findAuthorById(int id) throws SQLException {
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();

        if (resultSet.next()) {
            Author author = new Author();
            Integer author_id = resultSet.getInt("author_id");
            String first_name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");
            String country = resultSet.getString("country");

            if (author_id != null) {}
            return author;
        }
        return null;
    }
}
