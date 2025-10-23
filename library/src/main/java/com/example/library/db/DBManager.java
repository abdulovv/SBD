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

    //САША СДЕЛАЙ ЭТО
    private static void createSchema(Connection connection) throws SQLException {
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


