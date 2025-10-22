package com.example.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.SQLException;
import static com.example.library.db.DBManager.createDBIfNotExist;
import static com.example.library.db.DBManager.closeConnection;

@SpringBootApplication
public class LrApplication {

	public static void main(String[] args) throws SQLException {
		createDBIfNotExist();
		SpringApplication.run(LrApplication.class, args);
	}

}
