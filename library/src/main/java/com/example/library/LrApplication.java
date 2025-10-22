package com.example.library;

import com.example.library.db.DBManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class LrApplication {

	public static void main(String[] args) throws SQLException {
		DBManager manager = new DBManager();
		manager.createDBIfNotExist();

		SpringApplication.run(LrApplication.class, args);
		manager.closeConnection();
	}

}
