package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.db.entities.Bookshelf;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Data
@Repository
public class BookshelfRepository {
    public Bookshelf findBookshelfById(int id) throws SQLException {
        Bookshelf bookshelf = null;
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookshelfs WHERE bookshelf_id = ?");
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bookshelf = new Bookshelf();
                    Integer bookshelf_id = resultSet.getInt("bookshelf_id");
                    String name = resultSet.getString("bookshelf_name");
                    if (bookshelf_id != null && name != null) {
                        bookshelf.setBookshelf_name(name);
                        bookshelf.setBookshelf_id(bookshelf_id);
                    }
                }
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
        return bookshelf;
    }

    public void addBookshelf(Bookshelf bookshelf) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bookshelfs (bookshelf_name) VALUES (?)");
            preparedStatement.setString(1, bookshelf.getBookshelf_name());
            try {
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updateBookshelf(Bookshelf bookshelf) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bookshelfs SET bookshelf_name = ? WHERE bookshelf_id = ?");
            preparedStatement.setString(1, bookshelf.getBookshelf_name());
            preparedStatement.setInt(2, bookshelf.getBookshelf_id());
            try {
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void deleteBookshelf(Bookshelf bookshelf) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM bookshelfs WHERE bookshelf_id = ?");
            preparedStatement.setInt(1, bookshelf.getBookshelf_id());
            try {
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }
}
