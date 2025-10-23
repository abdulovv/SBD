package com.example.library.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public final class DBManager {
    private static final String ADMIN_URL = Dotenv.load().get("ADMIN_URL");
    private static final String DB_URL = Dotenv.load().get("DB_URL");
    private static final String DB_USERNAME = Dotenv.load().get("DB_USERNAME");
    private static final String PASSWORD = Dotenv.load().get("DB_PASSWORD");

    public static void createDBIfNotExist() throws SQLException {
        boolean isExist = databaseExists("library");

        if (!isExist){
            System.out.println("Db 'library' doesn't exist.");

            Connection connectionForAdminDB = DriverManager.getConnection(ADMIN_URL, DB_USERNAME, PASSWORD);
            Statement statement = connectionForAdminDB.createStatement();
            String query = "CREATE DATABASE library";
            statement.execute(query);

            System.out.println("Database 'library' was created!");
            createSchema(getConnection());
        }else {
            System.out.println("Database 'library' already exists!");
        }

        closeConnection();
    }

    private static void createSchema(Connection connection) throws SQLException {
        String createUsersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id SERIAL PRIMARY KEY," +
                        "first_name VARCHAR(100) NOT NULL," +
                        "last_name VARCHAR(100) NOT NULL," +
                        "phone VARCHAR(20)," +
                        "login VARCHAR(50) UNIQUE NOT NULL," +
                        "password VARCHAR(255) NOT NULL," +
                        "is_admin BOOLEAN NOT NULL DEFAULT FALSE);";


        String createGenreTable =
                "CREATE TABLE IF NOT EXISTS genre (" +
                        "genre_id SERIAL PRIMARY KEY," +
                        "genre_name VARCHAR(100) UNIQUE NOT NULL);";

        String createAuthorTable =
                "CREATE TABLE IF NOT EXISTS author (" +
                        "author_id SERIAL PRIMARY KEY," +
                        "first_name VARCHAR(100) NOT NULL," +
                        "last_name VARCHAR(100) NOT NULL," +
                        "country VARCHAR(100));";

        //---------------------------------------------------------//

        String createBookshelfTable =
                "CREATE TABLE IF NOT EXISTS bookshelf (" +
                        "bookshelf_id SERIAL PRIMARY KEY," +
                        "name VARCHAR(100) NOT NULL," +
                        "author_id INT," +
                        "FOREIGN KEY (author_id) REFERENCES author(author_id));";

        String createLogsTable =
                "CREATE TABLE IF NOT EXISTS logs (" +
                        "id SERIAL PRIMARY KEY," +
                        "datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "description TEXT," +
                        "user_id INT," +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id));";

        //---------------------------------------------------------//

        String createBookTable =
                "CREATE TABLE IF NOT EXISTS book (" +
                        "book_id SERIAL PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "year INT," +
                        "author_id INT NOT NULL," +
                        "genre_id INT NOT NULL," +
                        "bookshelf_id INT," +
                        "FOREIGN KEY (author_id) REFERENCES author(author_id)," +
                        "FOREIGN KEY (genre_id) REFERENCES genre(genre_id)," +
                        "FOREIGN KEY (bookshelf_id) REFERENCES bookshelf(bookshelf_id));";

        //---------------------------------------------------------//

        String createLoanTable =
                "CREATE TABLE IF NOT EXISTS loan (" +
                        "loan_id SERIAL PRIMARY KEY," +
                        "book_id INT NOT NULL," +
                        "user_id INT NOT NULL," +
                        "loan_date DATE NOT NULL," +
                        "return_date DATE," +
                        "FOREIGN KEY (book_id) REFERENCES book(book_id)," +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id));";

        try(Statement statement = connection.createStatement()){
            statement.execute(createUsersTable);
            statement.execute(createGenreTable);
            statement.execute(createAuthorTable);
            statement.execute(createBookshelfTable);
            statement.execute(createLogsTable);
            statement.execute(createBookTable);
            statement.execute(createLoanTable);
        }catch (SQLException e){
            System.out.println("Error in creating tables!");
        }

        System.out.println("Schema was created!");
    }

    private static boolean databaseExists(String dbName) throws SQLException {
        Connection adminConn = DriverManager.getConnection(ADMIN_URL, DB_USERNAME, PASSWORD);

        PreparedStatement stmt = adminConn.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?");
        stmt.setString(1, dbName);

        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, PASSWORD);
        }catch (SQLException e){
            System.out.println("Connection Failed!");
            connection = null;
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        getConnection().close();
    }
}


