package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.*;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Repository
public class BookRepository {
    public Book findBookById(int id) throws SQLException {
        Book book = null;
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT" +
                    "B.book_id, B.name, B.year, " +
                    "A.author_id, A.first_name, A.last_name, " +
                    "C.country_id, C.country_name, " +
                    "G.genre_id, G.genre_name, " +
                    "S.bookshelf_id, S.bookshelf_name " +
                    "FROM " +
                    "books AS B " +
                    "INNER JOIN " +
                    "authors AS A ON B.author_id = A.author_id " +
                    "INNER JOIN " +
                    "countries AS C ON A.country_id = C.country_id " +
                    "INNER JOIN " +
                    "genres AS G ON B.genre_id = G.genre_id " +
                    "INNER JOIN " +
                    "bookshelfs AS S ON B.bookshelf_id = S.bookshelf_id " +
                    "WHERE " +
                    "B.book_id = ?;"
            );
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()) {
                    Genre genre = new Genre();
                    Integer genre_id = resultSet.getInt("genre_id");
                    String genre_name = resultSet.getString("genre_name");
                    if (genre_id != null && genre_name != null) {
                        genre.setGenre_id(genre_id);
                        genre.setGenre_name(genre_name);
                    }

                    Country country = new Country();
                    Integer country_id = resultSet.getInt("country_id");
                    String country_name = resultSet.getString("country_name");
                    if (country_id != null && country_name != null) {
                        country.setCountry_id(country_id);
                        country.setCountry_name(country_name);
                    }

                    Author author = new Author();
                    Integer author_id = resultSet.getInt("author_id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    if (author_id != null && last_name != null && first_name != null) {
                        author.setAuthor_id(author_id);
                        author.setFirst_name(first_name);
                        author.setLast_name(last_name);
                        author.setCountry(country);
                    }

                    Bookshelf bookshelf = new Bookshelf();
                    Integer bookshelf_id = resultSet.getInt("bookshelf_id");
                    String bookshelf_name = resultSet.getString("bookshelf_name");
                    if (bookshelf_id != null && bookshelf_name != null) {
                        bookshelf.setBookshelf_id(bookshelf_id);
                        bookshelf.setBookshelf_name(bookshelf_name);
                    }

                    String name = resultSet.getString("name");
                    Integer year = resultSet.getInt("year");
                    if (name != null && year != null) {
                        book = new Book();
                        book.setName(name);
                        book.setYear(year);
                        book.setAuthor(author);
                        book.setBookshelf(bookshelf);
                        book.setGenre(genre);
                    }
                }
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
        return book;
    }

    public void addBook(Book book) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO books (book_id, bookshelf_id, genre_id, author_id, year, name) VALUES (?,?,?,?,?,?)");
            preparedStatement.setInt(1, book.getBook_id());
            preparedStatement.setInt(2, book.getBookshelf().getBookshelf_id());
            preparedStatement.setInt(3, book.getGenre().getGenre_id());
            preparedStatement.setInt(4, book.getAuthor().getAuthor_id());
            preparedStatement.setInt(5, book.getYear());
            preparedStatement.setString(6, book.getName());
            try{
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updateBook(Book book) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE books SET (author_id = ?, genre_id = ?, bookshelf_id = ?, name = ?, year = ?)");
            preparedStatement.setInt(1, book.getAuthor().getAuthor_id());
            preparedStatement.setInt(2,book.getGenre().getGenre_id());
            preparedStatement.setInt(3, book.getBookshelf().getBookshelf_id());
            preparedStatement.setString(4, book.getName());
            preparedStatement.setInt(5, book.getYear());
            try {
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void deleteBook(Book book) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM books WHERE book_id = ?");
            preparedStatement.setInt(1, book.getBook_id());
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




