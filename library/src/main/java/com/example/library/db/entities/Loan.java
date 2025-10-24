package com.example.library.db.entities;

import lombok.Data;
import java.sql.Date;

@Data
public class Loan {
    private Integer loan_id;
    private Book book;
    private User user;
    private Date loan_date;
    private Date return_date;
}
