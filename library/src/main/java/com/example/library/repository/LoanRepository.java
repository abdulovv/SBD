package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Loan;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class LoanRepository {
    public Loan findLoanById(int id) throws SQLException {
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM loan WHERE id = ?");
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();

        if (resultSet.next()) {
            Loan loan = new Loan();

        }

        return null;
    }
}
