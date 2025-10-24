package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.db.entities.Book;
import com.example.library.db.entities.Bookshelf;
import com.example.library.db.entities.Genre;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Data
@Repository
public class BookRepository {

    //КРЧ, НАДО СОСТАВИТЬ ЗАПРОС, КОТОРЫЙ ВОЗЬМЕТ ВСЕ ДАННЫЕ ЗА РАЗ, ЗАТЕМ ИХ ЗАПАРСИТЬ И ПОЛУЧИТЬ ОБЪЕКТ
    //ПОТОМУ ЧТО ДРУГИХ РЕПОЗИТОРЕВ НЕ ДОЛЖНО ТУТ БЫТЬ, НО Я СДЕЛАЛ, ЧТОБЫ СУТЬ БЫЛА ПОНЯТНО
    //РЕПОЗИТОРИИ АВТОРА, ЖАНРА ПО СУТИ СВОЕЙ ХАЛЯВА, ТК В НИХ НЕТУ ВНЕШНЕГО КЛЮЧА ИХ СРАЗУ МОЖНО ГАСИТЬ

    public Book findBookById(int id) throws SQLException {

        String sql = "SELECT b.book_id, b.name, b.year, " +
                "a_book.author_id AS author_id_book, a_book.first_name AS author_first_name, a_book.last_name AS author_last_name, a_book.country AS author_country, " +
                "g.genre_id, g.genre_name, " +
                "bs.bookshelf_id, bs.name AS bookshelf_name, " +
                "a_shelf.author_id AS author_id_shelf, a_shelf.first_name AS shelf_author_first_name, a_shelf.last_name AS shelf_author_last_name, a_shelf.country AS shelf_author_country " +
                "FROM book b " +
                "JOIN author a_book ON b.author_id = a_book.author_id " +
                "JOIN genre g ON b.genre_id = g.genre_id " +
                "LEFT JOIN bookshelf bs ON b.bookshelf_id = bs.bookshelf_id " +
                "LEFT JOIN author a_shelf ON bs.author_id = a_shelf.author_id " +
                "WHERE b.book_id = ?";



        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id); // Устанавливаем ID книги

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Author bookAuthor = new Author(
                            rs.getInt("author_id_book"),
                            rs.getString("author_first_name"),
                            rs.getString("author_last_name"),
                            rs.getString("author_country")
                    );

                    Genre bookGenre = new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("genre_name")
                    );

                    Bookshelf bookBookshelf = null;
                    if (rs.getObject("bookshelf_id") != null) {
                        Author shelfAuthor = null;
                        if (rs.getObject("author_id_shelf") != null) {
                            shelfAuthor = new Author(
                                    rs.getInt("author_id_shelf"),
                                    rs.getString("shelf_author_first_name"),
                                    rs.getString("shelf_author_last_name"),
                                    rs.getString("shelf_author_country")
                            );
                        }

                        bookBookshelf = new Bookshelf(
                                rs.getInt("bookshelf_id"),
                                rs.getString("bookshelf_name"),
                                shelfAuthor
                        );
                    }

                    Book book = new Book();
                    book.setBook_id(rs.getInt("book_id"));
                    book.setName(rs.getString("name"));
                    book.setYear(rs.getInt("year"));

                    book.setAuthor(bookAuthor);
                    book.setGenre(bookGenre);
                    book.setBookshelf(bookBookshelf);

                    return book;
                } else {
                    return null;
                }
            }
        }
    }

    public int addBook(Book book) throws SQLException {
        String INSERT_BOOK_SQL =
                "INSERT INTO book (name, year, author_id, genre_id, bookshelf_id) " +
                        "VALUES (?, ?, ?, ?, ?);";

        int generatedBookId = -1;
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_BOOK_SQL,
                     Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, book.getName());
            statement.setInt(2, book.getYear());

            statement.setInt(3, book.getAuthor().getAuthor_id());
            statement.setInt(4, book.getGenre().getGenre_id());

            if (book.getBookshelf() != null && book.getBookshelf().getShelf_id() != null) {
                statement.setInt(5, book.getBookshelf().getShelf_id());
            } else {
                statement.setNull(5, Types.INTEGER); // Устанавливаем NULL
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBookId = rs.getInt(1);
                        book.setBook_id(generatedBookId);
                    }
                }
            }
        }
        return generatedBookId;
    }

    public boolean updateBook(Book book) throws SQLException {
        String UPDATE_BOOK_SQL =
                "UPDATE book SET " +
                        "name = ?, year = ?, author_id = ?, genre_id = ?, bookshelf_id = ? " +
                        "WHERE book_id = ?;";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BOOK_SQL))
        {
            statement.setString(1, book.getName());
            statement.setInt(2, book.getYear());

            statement.setInt(3, book.getAuthor().getAuthor_id());
            statement.setInt(4, book.getGenre().getGenre_id());

            if (book.getBookshelf() != null && book.getBookshelf().getShelf_id() != null) {
                statement.setInt(5, book.getBookshelf().getShelf_id());
            } else {
                statement.setNull(5, Types.INTEGER); // Устанавливаем NULL, если полка отсутствует
            }

            statement.setInt(6, book.getBook_id());
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean deleteBook(Book book) throws SQLException {
        String DELETE_BOOK_SQL =
                "DELETE FROM book " +
                        "WHERE book_id = ?;";

        if (book == null || book.getBook_id() == null) {
            System.err.println("Ошибка: Объект книги или ее ID не может быть NULL для удаления.");
            return false;
        }

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BOOK_SQL))
        {
            statement.setInt(1, book.getBook_id());
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }

}

