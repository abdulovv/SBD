package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.*;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class LoanRepository {
    public Loan getLoanById(int id) throws SQLException {
         /*
        SELECT
                l.loan_id, l.loan_date, l.return_date,
                -- Данные Пользователя (User)
                u.user_id, u.login, u.password, u.first_name, u.phone, u.last_name, u.isAdmin,
                -- Данные Книги (Book)
                b.book_id, b.name AS book_name, b.year,
                -- Данные Автора Книги (Author)
                a_book.author_id AS author_id_book, a_book.first_name AS author_first_name, a_book.last_name AS author_last_name, a_book.country AS author_country,
                -- Данные Жанра (Genre)
                g.genre_id, g.genre_name,
                -- Данные Полки (Bookshelf)
                bs.bookshelf_id, bs.name AS bookshelf_name,
                -- Данные Куратора Полки (Author, связанный с Bookshelf)
                a_shelf.author_id AS author_id_shelf, a_shelf.first_name AS shelf_author_first_name, a_shelf.last_name AS shelf_author_last_name, a_shelf.country AS shelf_author_country
            FROM
                loan l
            JOIN
                users u ON l.user_id = u.user_id
            JOIN
                book b ON l.book_id = b.book_id
            JOIN
                author a_book ON b.author_id = a_book.author_id
            JOIN
                genre g ON b.genre_id = g.genre_id
            LEFT JOIN
                bookshelf bs ON b.bookshelf_id = bs.bookshelf_id
            LEFT JOIN
                author a_shelf ON bs.author_id = a_shelf.author_id
            WHERE
                l.loan_id = ?;

        * */

        // Здесь используется SQL-запрос, определенный выше (многострочный для читаемости)
        String sql = "SELECT l.loan_id, l.loan_date, l.return_date, u.user_id, u.login, u.password, u.first_name, u.phone, u.last_name, u.isAdmin, b.book_id, b.name AS book_name, b.year, a_book.author_id AS author_id_book, a_book.first_name AS author_first_name, a_book.last_name AS author_last_name, a_book.country AS author_country, g.genre_id, g.genre_name, bs.bookshelf_id, bs.name AS bookshelf_name, a_shelf.author_id AS author_id_shelf, a_shelf.first_name AS shelf_author_first_name, a_shelf.last_name AS shelf_author_last_name, a_shelf.country AS shelf_author_country FROM loan l JOIN users u ON l.user_id = u.user_id JOIN book b ON l.book_id = b.book_id JOIN author a_book ON b.author_id = a_book.author_id JOIN genre g ON b.genre_id = g.genre_id LEFT JOIN bookshelf bs ON b.bookshelf_id = bs.bookshelf_id LEFT JOIN author a_shelf ON bs.author_id = a_shelf.author_id WHERE l.loan_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {

                    // 1. Создание объекта User
                    User user = new User();
                    user.setUser_id(rs.getInt("user_id"));
                    user.setLogin(rs.getString("login"));
                    user.setPassword(rs.getString("password"));
                    user.setFirst_name(rs.getString("first_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setLast_name(rs.getString("last_name"));
                    user.setIsAdmin(rs.getBoolean("isAdmin"));

                    // 2. Создание объектов внутри Book (Author, Genre, Bookshelf)

                    // Автор книги
                    Author bookAuthor = new Author(
                            rs.getInt("author_id_book"),
                            rs.getString("author_first_name"),
                            rs.getString("author_last_name"),
                            rs.getString("author_country")
                    );

                    // Жанр
                    Genre bookGenre = new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("genre_name")
                    );

                    // Полка (Bookshelf) - может быть NULL
                    Bookshelf bookBookshelf = null;
                    if (rs.getObject("bookshelf_id") != null) {
                        // Куратор полки (может быть NULL)
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

                    // 3. Создание объекта Book
                    Book book = new Book();
                    book.setBook_id(rs.getInt("book_id"));
                    book.setName(rs.getString("book_name"));
                    book.setYear(rs.getInt("year"));
                    book.setAuthor(bookAuthor);
                    book.setGenre(bookGenre);
                    book.setBookshelf(bookBookshelf);

                    // 4. Создание объекта Loan
                    Loan loan = new Loan();
                    loan.setLoan_id(rs.getInt("loan_id"));
                    // Преобразуем java.sql.Date в java.util.Date
                    loan.setLoan_date(rs.getDate("loan_date") != null ? new Date(rs.getDate("loan_date").getTime()) : null);
                    loan.setReturn_date(rs.getDate("return_date") != null ? new Date(rs.getDate("return_date").getTime()) : null);
                    loan.setBook(book);
                    loan.setUser(user);

                    return loan;
                }
            }
        }
        return null;
    }

    public int addLoan(Loan loan) throws SQLException {
        String INSERT_LOAN_SQL =
                "INSERT INTO loan (book_id, user_id, loan_date, return_date) " +
                        "VALUES (?, ?, ?, ?)";

        int generatedId = -1;

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_LOAN_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Используем ID из вложенных объектов
            statement.setInt(1, loan.getBook().getBook_id());
            statement.setInt(2, loan.getUser().getUser_id());

            // loan_date
            statement.setDate(3, new java.sql.Date(loan.getLoan_date().getTime()));

            // return_date (может быть null)
            if (loan.getReturn_date() != null) {
                statement.setDate(4, new java.sql.Date(loan.getReturn_date().getTime()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        loan.setLoan_id(generatedId);
                    }
                }
            }
        }
        return generatedId;
    }

    public boolean updateLoan(Loan loan, int loanId) throws SQLException {
        String UPDATE_LOAN_SQL =
                "UPDATE loan SET book_id = ?, user_id = ?, loan_date = ?, return_date = ? " +
                        "WHERE loan_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_LOAN_SQL)) {

            statement.setInt(1, loan.getBook().getBook_id());
            statement.setInt(2, loan.getUser().getUser_id());

            // loan_date
            statement.setDate(3, new java.sql.Date(loan.getLoan_date().getTime()));

            // return_date (может быть NULL)
            if (loan.getReturn_date() != null) {
                statement.setDate(4, new java.sql.Date(loan.getReturn_date().getTime()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            // ID для WHERE
            statement.setInt(5, loanId);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;
        }
    }

    public boolean deleteLoan(Loan loan) throws SQLException {
        String DELETE_LOAN_SQL =
                "DELETE FROM loan WHERE loan_id = ?";

        if (loan == null || loan.getLoan_id() == null) {
            System.err.println("Ошибка: Объект займа или его ID не может быть NULL для удаления.");
            return false;
        }

        int loanId = loan.getLoan_id();

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_LOAN_SQL)) {

            statement.setInt(1, loanId);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;
        }
    }


}
