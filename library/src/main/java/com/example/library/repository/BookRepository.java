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

                    // 1. Создание объекта Author (Автор КНИГИ)
                    Author bookAuthor = new Author(
                            rs.getInt("author_id_book"),
                            rs.getString("author_first_name"),
                            rs.getString("author_last_name"),
                            rs.getString("author_country")
                    );

                    // 2. Создание объекта Genre
                    Genre bookGenre = new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("genre_name")
                    );

                    // 3. Создание объекта Bookshelf (Сложно, т.к. может быть NULL)
                    Bookshelf bookBookshelf = null;
                    if (rs.getObject("bookshelf_id") != null) {

                        // Создание объекта Author (Куратор ПОЛКИ), который может быть NULL
                        Author shelfAuthor = null;
                        if (rs.getObject("author_id_shelf") != null) {
                            shelfAuthor = new Author(
                                    rs.getInt("author_id_shelf"),
                                    rs.getString("shelf_author_first_name"),
                                    rs.getString("shelf_author_last_name"),
                                    rs.getString("shelf_author_country")
                            );
                        }

                        // Создание самой Полки
                        bookBookshelf = new Bookshelf(
                                rs.getInt("bookshelf_id"),
                                rs.getString("bookshelf_name"),
                                shelfAuthor
                        );
                    }

                    // 4. Создание объекта Book
                    Book book = new Book();
                    book.setBook_id(rs.getInt("book_id"));
                    book.setName(rs.getString("name"));
                    book.setYear(rs.getInt("year"));

                    // Установка вложенных объектов
                    book.setAuthor(bookAuthor);
                    book.setGenre(bookGenre);
                    book.setBookshelf(bookBookshelf); // Может быть NULL

                    return book;

                } else {
                    return null; // Книга не найдена
                }
            }
        }
    }


    /**
     * Добавляет новую книгу в базу данных.
     * * @param book Объект книги, который нужно добавить.
     * @return Сгенерированный book_id, или -1 в случае ошибки.
     * @throws SQLException Если произошла ошибка при работе с базой данных.
     */
    public int addBook(Book book) throws SQLException {
        String INSERT_BOOK_SQL =
                "INSERT INTO book (name, year, author_id, genre_id, bookshelf_id) " +
                        "VALUES (?, ?, ?, ?, ?);";

        int generatedBookId = -1;

        // Использование try-with-resources для автоматического закрытия Connection и Statement
        try (Connection connection = DBManager.getConnection();
             // Указываем RETURN_GENERATED_KEYS, чтобы получить book_id
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_BOOK_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            // 1. Установка параметров в PreparedStatement
            statement.setString(1, book.getName());
            statement.setInt(2, book.getYear());

            // Получаем ID из вложенных объектов
            statement.setInt(3, book.getAuthor().getAuthor_id());
            statement.setInt(4, book.getGenre().getGenre_id());

            // Bookshelf_id может быть NULL.
            if (book.getBookshelf() != null && book.getBookshelf().getShelf_id() != null) {
                statement.setInt(5, book.getBookshelf().getShelf_id());
            } else {
                statement.setNull(5, Types.INTEGER); // Устанавливаем NULL
            }

            // 2. Выполнение запроса
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                // 3. Получение сгенерированного book_id
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBookId = rs.getInt(1);
                        // Обновляем ID в Java-объекте
                        book.setBook_id(generatedBookId);
                    }
                }
            }
        }
        return generatedBookId;
    }

}

