package com.example.db;

import com.example.db.entities.Book;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class DBManager {
    private final String ADMIN_URL = "jdbc:postgresql://localhost:5432/postgres";

    private final String url;
    private final String username;
    private final String password;

    private Connection connection;

    public DBManager(){
        Dotenv dotenv = Dotenv.load();
        url = dotenv.get("DB_URL");
        username = dotenv.get("DB_USERNAME");
        password = dotenv.get("DB_PASSWORD");
    }

    public void createDBIfNotExist() throws SQLException {
        boolean isExist = databaseExists("library");

        if (!isExist){
            System.out.println("Db 'library' doesn't exist.");

            Connection connection = DriverManager.getConnection(ADMIN_URL, username, password);
            Statement statement = connection.createStatement();
            String query = "CREATE DATABASE library";
            statement.execute(query);

            System.out.println("Database 'library' was created!");
            createSchema(connection);
        }else {
            System.out.println("Database 'library' already exists!");
        }

        getConnection();
    }

    private void createSchema(Connection connection) throws SQLException {
        String createUsersTable =
                       "CREATE TABLE users (id SERIAL PRIMARY KEY," +
                       "full_name VARCHAR(100) NOT NULL," +
                       "registration_date DATE NOT NULL," +
                       "login VARCHAR(50) UNIQUE NOT NULL," +
                       "password VARCHAR(255) NOT NULL," +
                       "is_admin BOOLEAN NOT NULL DEFAULT FALSE);";

        String createBooksTable =
                       "CREATE TABLE books (id SERIAL PRIMARY KEY," +
                       "author VARCHAR(100) NOT NULL," +
                       "title VARCHAR(200) NOT NULL," +
                       "in_stock BOOLEAN NOT NULL DEFAULT TRUE);";

        String createLoansTable =
                "CREATE TABLE loan (id SERIAL PRIMARY KEY," +
                "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE," +
                "book_id INTEGER NOT NULL REFERENCES books(id) ON DELETE CASCADE," +
                "loan_date DATE NOT NULL," +
                "return_date DATE);";

        String createLogsTable =
                "CREATE TABLE logs (id SERIAL PRIMARY KEY," +
                "user_id INTEGER REFERENCES users(id) ON DELETE SET NULL," +
                "action VARCHAR(50) NOT NULL," +
                "action_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "details TEXT);";

        try(Statement statement = connection.createStatement()){
            statement.execute(createUsersTable);
            statement.execute(createBooksTable);
            statement.execute(createLoansTable);
            statement.execute(createLogsTable);
        }catch (SQLException e){
            System.out.println("Error in creating tables!");
        }

        System.out.println("Schema was created!");
    }


    private boolean databaseExists(String dbName) throws SQLException {
        Connection adminConn = DriverManager.getConnection(ADMIN_URL, username, password);

        PreparedStatement stmt = adminConn.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?");
        stmt.setString(1, dbName);

        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    }

    private void getConnection() throws SQLException {
        try {
            connection = DriverManager.getConnection(url, username, password);
        }catch (SQLException e){
            System.out.println("Connection Failed!");
            connection = null;
        }
    }

    public void addBook() throws SQLException {
        String query = "INSERT INTO books (author, title, in_stock) VALUES (?, ?, ?);";

        Book book = new Book();
        PreparedStatement statement = connection.prepareStatement(query);
        connection.setAutoCommit(false);

        statement.setString(1, book.getAuthor());
        statement.setString(2, book.getTitle());
        statement.setBoolean(3, book.getIn_stock());
        statement.execute();
        connection.commit();

        System.out.println("Book added successfully!");
    }

    public Book getBookById(int id) throws SQLException {
        String query = "SELECT id, author, title, in_stock FROM books WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int bookId = rs.getInt("id");
                    String author = rs.getString("author");
                    String title = rs.getString("title");
                    boolean inStock = rs.getBoolean("in_stock");

                    return new Book(bookId, author, title, inStock);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getting book by ID: " + e.getMessage());
            return  null;
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}


