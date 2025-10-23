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
    public Genre findGenreById(int id) throws SQLException {
        Genre genre = null;
        try (Connection connection = DBManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT genre_id, genre_name FROM genre WHERE genre_id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    genre = new Genre();
                    Integer genre_id = resultSet.getInt("genre_id");
                    String genre_name = resultSet.getString("genre_name");

                    if (genre_id != null &&  genre_name != null) {
                        genre.setGenre_id(genre_id);
                        genre.setGenre_name(genre_name);
                    }
                }
            }
        }finally {
            DBManager.closeConnection();
        }
        return genre;
    }

    public void addGenre(Genre genre) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO genre (genre_name) VALUES (?)");
            preparedStatement.setString(1,genre.getGenre_name());
            preparedStatement.executeUpdate();
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updateGenre(Genre genre) throws SQLException {
        try (Connection connection = DBManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("UPDATE genre SET genre_name = ? WHERE genre_id = ?")
        ){
            pstmt.setString(1, genre.getGenre_name());
            pstmt.setInt(2, genre.getGenre_id());
            pstmt.executeUpdate();
        } finally {
            DBManager.closeConnection();
        }
    }

    public void deleteGenre(Genre genre) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM genre WHERE genre_id = ?");
            preparedStatement.setInt(1, genre.getGenre_id());
            preparedStatement.executeUpdate();
        }finally {
            DBManager.closeConnection();
        }
    }
}
