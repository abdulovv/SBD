package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.PersonalData;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PersonalDataRepository {
    public PersonalData findPersonalDataById(int id) throws SQLException {
        PersonalData personalData = null;
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM personaldata WHERE data_id = ?");
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    personalData = new PersonalData();
                    Integer data_id = resultSet.getInt("data_id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String number = resultSet.getString("number");

                    if (data_id != null && first_name != null && last_name != null && number != null) {
                        personalData.setData_id(data_id);
                        personalData.setFirst_name(first_name);
                        personalData.setLast_name(last_name);
                        personalData.setNumber(number);
                    }
                }
            }catch(SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }

        return personalData;
    }

    public void addPersonalData(PersonalData personalData) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO personaldata (first_name, last_name, number) VALUES (?, ?, ?)");
            preparedStatement.setString(1, personalData.getFirst_name());
            preparedStatement.setString(2, personalData.getLast_name());
            preparedStatement.setString(3, personalData.getNumber());
            try{
               preparedStatement.executeUpdate();
            }catch(SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updatePersonalData(PersonalData personalData) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE personaldata SET first_name = ?, last_name = ?, number = ? WHERE data_id = ?");
            preparedStatement.setString(1, personalData.getFirst_name());
            preparedStatement.setString(2, personalData.getLast_name());
            preparedStatement.setString(3, personalData.getNumber());
            preparedStatement.setInt(4, personalData.getData_id());
            try {
                preparedStatement.executeUpdate();
            }catch(SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void deletePersonalData(PersonalData personalData) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM personaldata WHERE data_id = ?");
            preparedStatement.setInt(1, personalData.getData_id());
            try {
                preparedStatement.executeUpdate();
            }catch(SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }
}
