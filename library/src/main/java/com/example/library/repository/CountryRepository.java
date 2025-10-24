package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Country;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CountryRepository {
    public Country findByCountryByid(int id) throws SQLException {
        Country country = null;
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM countries WHERE country_id = ?");
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    Integer country_id = resultSet.getInt("country_id");
                    String country_name = resultSet.getString("country_name");
                    if (country_name != null && country_id != null){
                        country = new Country();
                        country.setCountry_id(country_id);
                        country.setCountry_name(country_name);
                    }
                }
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
        return country;
    }

    public void addCountry(Country country) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO countries (country_name) VALUES (?)");
            preparedStatement.setString(1, country.getCountry_name());
            try{
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updateCountry(Country country) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE countries SET country_name = ? WHERE country_id = ?");
            preparedStatement.setString(1, country.getCountry_name());
            preparedStatement.setInt(2, country.getCountry_id());
            try {
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void deleteCountry(Country country) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM countries WHERE country_id = ?");
            preparedStatement.setInt(1, country.getCountry_id());
            try {
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }
}
