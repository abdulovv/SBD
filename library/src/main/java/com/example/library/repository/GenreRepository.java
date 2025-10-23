package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.db.entities.Genre;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



@Repository
public class GenreRepository {
    //ТУТ ХАЛЯВА, СМОТРИ МЕТОД ПОИСКА ПО ID В BOOKREPOSITORY, Я ВСЕ РАСПИСАЛ

    public Genre findGenreById(int id) throws SQLException {
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM genre WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();

        if (resultSet.next()) {
            Genre genre = new Genre();
            genre.setGenre_id(resultSet.getInt("genre_id"));
            genre.setGenre_name(resultSet.getString("genre_name"));
            return genre;
        }
        return null;
    }
}
