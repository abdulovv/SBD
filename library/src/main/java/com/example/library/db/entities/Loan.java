package com.example.library.db.entities;

import lombok.Data;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Loan {
    private Integer loan_id;
    private Book book;
    private User user;
    private Date loan_date;
    private Date return_date;

//    public static Loan convertToObjectFromResultset(ResultSet resultSet) throws SQLException {
//        Loan loan = new Loan();
//        loan.setLoan_id(resultSet.getInt("loan_id"));
//        loan.setBook_id(resultSet.getInt("book_id"));
//        loan.setUser_id(resultSet.getInt("user_id"));
//        loan.setLoan_date(resultSet.getDate("loan_date"));
//        loan.setReturn_date(resultSet.getDate("return_date"));
//        return loan;
//    }
}
