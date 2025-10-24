package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.db.entities.Country;
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
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT " +
                        "A.author_id, A.first_name, A.last_name, " +
                        "C.country_id, C.country_name " +
                        "FROM " +
                        "authors AS A " +
                        "INNER JOIN " +
                        "countries AS C " +
                        "ON A.country_id = C.country_id " +
                        "WHERE A.author_id = ?");
        ){
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    author = new Author();
                    Integer author_id = resultSet.getInt("author_id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");

                    Integer country_id = resultSet.getInt("country_id");
                    String country_name = resultSet.getString("country_name");

                    if (author_id != null && first_name != null && last_name != null && country_id != null &&  country_id != null) {
                        author.setAuthor_id(author_id);
                        author.setFirst_name(first_name);
                        author.setLast_name(last_name);

                        Country country = new Country(country_id, country_name);
                        author.setCountry(country);
                    }
                }
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
        return author;
    }

    public void addAuthor(Author author) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO authors (country_id, first_name, last_name) VALUES (?, ?, ?)");
            preparedStatement.setInt(1, author.getCountry().getCountry_id());
            preparedStatement.setString(2, author.getFirst_name());
            preparedStatement.setString(3, author.getLast_name());
            try{
                preparedStatement.executeUpdate();
            }catch(SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updateAuthor(Author author) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE author SET first_name = ?, last_name = ?, country_id = ? WHERE author_id = ?");
            preparedStatement.setString(1, author.getFirst_name());
            preparedStatement.setString(2, author.getLast_name());
            preparedStatement.setInt(3, author.getCountry().getCountry_id());
            preparedStatement.setInt(4, author.getAuthor_id());
            try {
                preparedStatement.executeUpdate();
            }catch(SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
            }
        } finally {
            DBManager.closeConnection();
        }
    }

    public void deleteAuthor(Author author) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM authors WHERE author_id = ?");
            preparedStatement.setInt(1, author.getAuthor_id());
            try {
                preparedStatement.executeUpdate();
            }catch(SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }
}
