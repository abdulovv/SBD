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
        String createCountriesTable =
                "CREATE TABLE IF NOT EXISTS Countries " +
                "(country_id SERIAL PRIMARY KEY, " +
                "country_name VARCHAR(255) NOT NULL UNIQUE)";

        String createGenresTable =
                "CREATE TABLE IF NOT EXISTS Genres " +
                "(genre_id SERIAL PRIMARY KEY, " +
                "genre_name VARCHAR(255) NOT NULL UNIQUE)";

        String createBookshelfsTable =
                "CREATE TABLE IF NOT EXISTS Bookshelfs " +
                "(bookshelf_id SERIAL PRIMARY KEY, " +
                "bookshelf_name VARCHAR(255) NOT NULL UNIQUE)";

        String createActionsTable =
                "CREATE TABLE IF NOT EXISTS Actions " +
                "(action_id SERIAL PRIMARY KEY, " +
                "description VARCHAR(255) NOT NULL UNIQUE)";

        String createAuthorsTable =
                "CREATE TABLE IF NOT EXISTS Authors " +
                "(author_id SERIAL PRIMARY KEY, " +
                "country_id INT NOT NULL, " +
                "first_name VARCHAR(255) NOT NULL, " +
                "last_name VARCHAR(255) NOT NULL, " +
                "FOREIGN KEY (country_id) REFERENCES Countries (country_id))";

        String createUsersTable =
                "CREATE TABLE IF NOT EXISTS Users " +
                "(user_id SERIAL PRIMARY KEY, " +
                "data_id INT UNIQUE, " +
                "login VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, i" +
                "sAdmin BOOLEAN NOT NULL DEFAULT FALSE)";

        String createPersonalDataTable =
                "CREATE TABLE IF NOT EXISTS PersonalData " +
                "(data_id SERIAL PRIMARY KEY, " +
                "first_name VARCHAR(255) NOT NULL, " +
                "last_name VARCHAR(255) NOT NULL, number VARCHAR(255) NOT NULL UNIQUE, " +
                "FOREIGN KEY (data_id) REFERENCES Users (data_id))";

        String createBooksTable =
                "CREATE TABLE IF NOT EXISTS Books " +
                "(book_id SERIAL PRIMARY KEY, " +
                "author_id INT NOT NULL, " +
                "genre_id INT NOT NULL, " +
                "bookshelf_id INT, " +
                "name VARCHAR(255) NOT NULL, " +
                "year INT, " +
                "FOREIGN KEY (author_id) REFERENCES Authors (author_id), " +
                "FOREIGN KEY (genre_id) REFERENCES Genres (genre_id), " +
                "FOREIGN KEY (bookshelf_id) REFERENCES Bookshelfs (bookshelf_id))";

        String createLogsTable =
                "CREATE TABLE IF NOT EXISTS Logs " +
                "(log_id SERIAL PRIMARY KEY, " +
                "user_id INT, " +
                "action_id INT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES Users (user_id), " +
                "FOREIGN KEY (action_id) REFERENCES Actions (action_id))";

        String createLoansTable =
                "CREATE TABLE IF NOT EXISTS Loans " +
                "(loan_id SERIAL PRIMARY KEY, " +
                "book_id INT NOT NULL, " +
                "user_id INT NOT NULL, " +
                "loan_date DATE NOT NULL, " +
                "return_date DATE, " +
                "FOREIGN KEY (book_id) REFERENCES Books (book_id), " +
                "FOREIGN KEY (user_id) REFERENCES Users (user_id))";


        try(Statement statement = connection.createStatement()){
            statement.execute(createCountriesTable);
            statement.execute(createGenresTable);
            statement.execute(createBookshelfsTable);
            statement.execute(createActionsTable);
            statement.execute(createUsersTable);
            statement.execute(createPersonalDataTable);
            statement.execute(createAuthorsTable);
            statement.execute(createBooksTable);
            statement.execute(createLogsTable);
            statement.execute(createLoansTable);
        }catch (SQLException e){
            System.out.println("Error in creating tables!");
            e.printStackTrace();
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


