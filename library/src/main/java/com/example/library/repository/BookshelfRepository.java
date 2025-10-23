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

    //СМОТРИ МЕТОД ПОИСКА ПО ID В BOOKREPOSITORY, Я ВСЕ РАСПИСАЛ
    public Bookshelf findBookshelfById(int id) throws SQLException {
        String sql = "SELECT bs.bookshelf_id, bs.name AS bookshelf_name, " +
                "a.author_id, a.first_name, a.last_name, a.country " +
                "FROM bookshelf bs LEFT JOIN author a ON bs.author_id = a.author_id " +
                "WHERE bs.bookshelf_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // 1. Создание объекта Author (Куратор полки, может быть NULL)
                    Author shelfAuthor = null;
                    if (rs.getObject("author_id") != null) {
                        shelfAuthor = new Author(
                                rs.getInt("author_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("country")
                        );
                    }

                    // 2. Создание объекта Bookshelf
                    return new Bookshelf(
                            rs.getInt("bookshelf_id"),
                            rs.getString("bookshelf_name"),
                            shelfAuthor
                    );
                }
            }
        }
        return null;
    }

    public int createBookshelf(Bookshelf bookshelf) throws SQLException {
        int generatedId = -1;
        String sql = "INSERT INTO bookshelf (name, author_id) VALUES (?, ?)";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, bookshelf.getName());

            // Автор-куратор может быть NULL
            if (bookshelf.getAuthor() != null && bookshelf.getAuthor().getAuthor_id() != null) {
                statement.setInt(2, bookshelf.getAuthor().getAuthor_id());
            } else {
                statement.setNull(2, Types.INTEGER); // Устанавливаем NULL
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        bookshelf.setShelf_id(generatedId);
                    }
                }
            }
        }
        return generatedId;
    }

    public boolean updateBookshelf(Bookshelf bookshelf) throws SQLException {
        String sql = "UPDATE bookshelf SET name = ?, author_id = ? WHERE bookshelf_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, bookshelf.getName());

            // Автор-куратор может быть NULL
            if (bookshelf.getAuthor() != null && bookshelf.getAuthor().getAuthor_id() != null) {
                statement.setInt(2, bookshelf.getAuthor().getAuthor_id());
            } else {
                statement.setNull(2, Types.INTEGER); // Устанавливаем NULL
            }

            // ID для WHERE
            statement.setInt(3, bookshelf.getShelf_id());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Удаляет полку из базы данных.
     * ВНИМАНИЕ: Если на этой полке есть книги (поле book.bookshelf_id ссылается на нее),
     * база данных выдаст ошибку ForeignKeyViolation.
     */
    public boolean deleteBookshelf(Bookshelf bookshelf) throws SQLException {

        if (bookshelf == null || bookshelf.getShelf_id() == null) {
            System.err.println("Ошибка: Объект полки или ее ID не может быть NULL для удаления.");
            return false;
        }

        int bookshelfId = bookshelf.getShelf_id();
        String sql = "DELETE FROM bookshelf WHERE bookshelf_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookshelfId);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;
        }
    }


    /**
     * Удаляет полку и все книги, которые на ней находятся.
     * Операция выполняется в рамках одной транзакции.
     *
     * @param bookshelf Объект Bookshelf, содержащий ID полки.
     * @return true, если удаление прошло успешно (транзакция зафиксирована), иначе false.
     * @throws SQLException Если произошла ошибка при работе с базой данных.
     */
    public boolean deleteBookshelfAndBooks(Bookshelf bookshelf) throws SQLException {
        String DELETE_BOOKS_ON_SHELF_SQL =
                "DELETE FROM book WHERE bookshelf_id = ?;";

        String DELETE_BOOKSHELF_SQL =
                "DELETE FROM bookshelf WHERE bookshelf_id = ?;";

        if (bookshelf == null || bookshelf.getShelf_id() == null) {
            System.err.println("Ошибка: Объект полки или ее ID не может быть NULL для удаления.");
            return false;
        }

        int bookshelfId = bookshelf.getShelf_id();

        // Начинаем транзакцию: получаем соединение
        try (Connection connection = DBManager.getConnection()) {

            // 1. Отключаем автокоммит
            connection.setAutoCommit(false);

            try (
                    // PreparedStatement для удаления книг
                    PreparedStatement deleteBooksStmt = connection.prepareStatement(DELETE_BOOKS_ON_SHELF_SQL);
                    // PreparedStatement для удаления полки
                    PreparedStatement deleteShelfStmt = connection.prepareStatement(DELETE_BOOKSHELF_SQL)
            ) {
                // --- 1. Удаляем все книги на этой полке ---
                deleteBooksStmt.setInt(1, bookshelfId);
                int deletedBooks = deleteBooksStmt.executeUpdate();
                System.out.println("Удалено книг с полки " + bookshelfId + ": " + deletedBooks);

                // --- 2. Удаляем саму полку ---
                deleteShelfStmt.setInt(1, bookshelfId);
                int affectedRows = deleteShelfStmt.executeUpdate();

                if (affectedRows > 0) {
                    // 3. Если полка удалена, фиксируем транзакцию
                    connection.commit();
                    return true;
                } else {
                    // 4. Если полка не найдена (0 строк), откатываем изменения (хотя книги уже удалились бы,
                    // лучше откатить, чтобы избежать частичных изменений)
                    connection.rollback();
                    return false;
                }

            } catch (SQLException e) {
                // В случае любой ошибки, откатываем все изменения
                connection.rollback();
                throw e; // Перебрасываем исключение для обработки на более высоком уровне
            } finally {
                // Важно: возвращаем соединение в режим автокоммита
                connection.setAutoCommit(true);
            }
        }
    }


}
