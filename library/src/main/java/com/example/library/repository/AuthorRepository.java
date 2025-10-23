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
    public Author findAuthorById(int id) throws SQLException {
        Author author = null;
        try (Connection connection = DBManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT author_id, first_name, last_name, country FROM author WHERE author_id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    author = new Author();
                    Integer authorId = resultSet.getInt("author_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String country = resultSet.getString("country");

                    if (authorId != null &&  firstName != null && lastName != null && country != null) {
                        author.setAuthor_id(authorId);
                        author.setFirst_name(firstName);
                        author.setLast_name(lastName);
                        author.setCountry(country);
                    }
                }
            }
        }finally {
            DBManager.closeConnection();
        }
        return author;
    }

    public void addAuthor(Author author) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO author (first_name, last_name, country) VALUES (?, ?, ?)");
            preparedStatement.setString(1,author.getFirst_name());
            preparedStatement.setString(2,author.getLast_name());
            preparedStatement.setString(3,author.getCountry());
            preparedStatement.executeUpdate();
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updateAuthor(Author author, int id) throws SQLException {
        try (Connection connection = DBManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("UPDATE author SET first_name = ?, last_name = ?, country = ? WHERE author_id = ?")
        ){
            pstmt.setString(1, author.getFirst_name());
            pstmt.setString(2, author.getLast_name());
            pstmt.setString(3, author.getCountry());
            pstmt.setInt(4, id);

            pstmt.executeUpdate();
        } finally {
            DBManager.closeConnection();
        }
    }

    public void deleteAuthor(int id) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM author WHERE author_id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }finally {
            DBManager.closeConnection();
        }
    }
}
