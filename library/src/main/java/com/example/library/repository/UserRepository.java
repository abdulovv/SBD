package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.PersonalData;
import com.example.library.db.entities.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {
    public User findUserById(int id) throws SQLException {
        User user = null;
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT " +
                    "U.user_id, U.login, U.password, U.isadmin, " +
                    "D.data_id, D.first_name, D.last_name, D.number " +
                    "FROM " +
                    "users AS U " +
                    "JOIN " +
                    "personaldata AS D " +
                    "ON U.data_id = D.data_id " +
                    "WHERE U.user_id = ?"
            );
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    user = new User();
                    Integer user_id = resultSet.getInt("user_id");
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    Boolean isAdmin = resultSet.getBoolean("isadmin");

                    PersonalData  personalData = new PersonalData();

                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String number = resultSet.getString("number");
                    Integer data_id = resultSet.getInt("data_id");

                    if (firstName != null && lastName != null && number != null && data_id != null){
                        personalData.setFirst_name(firstName);
                        personalData.setLast_name(lastName);
                        personalData.setNumber(number);
                        personalData.setData_id(data_id);
                        if (user_id != null && login != null && password != null){
                            user.setLogin(login);
                            user.setPassword(password);
                            user.setIsAdmin(isAdmin);
                            user.setPersonalData(personalData);
                            user.setUser_id(user_id);
                        }
                    }
                }
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
        return user;
    }

    /** This method also creates user's personal data*/
    public void addUser(User user) throws SQLException {
        try{
            int data_id = addPersonalData(user.getPersonalData());
            try (Connection connection = DBManager.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Users (data_id, login, password, isAdmin) VALUES (?, ?, ?, ?)");
                preparedStatement.setInt(1, data_id);
                preparedStatement.setString(2, user.getLogin());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setBoolean(4, user.getIsAdmin());

                try {
                    preparedStatement.executeUpdate();
                }catch (SQLException e){
                    deletePersonalData(user.getPersonalData());
                    System.out.println("SQLException: " + e.getMessage());
                }

            }finally {
                DBManager.closeConnection();
            }
        }catch (SQLException e){
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    /** This method changes only password and personal data. */
    public void updateUser(User user) throws SQLException {
        try (Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET password = ? WHERE user_id = ?");
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setInt(2, user.getUser_id());
            try {
                preparedStatement.executeUpdate();
                updatePersonalData(user.getPersonalData());
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    /** This method also removes user's personal data*/
    public void deleteUser(User user) throws SQLException {
        try (Connection connection = DBManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?");
            preparedStatement.setInt(1, user.getUser_id());
            try {
                preparedStatement.executeUpdate();
                try {
                    deletePersonalData(user.getPersonalData());
                }catch (SQLException e){
                    addUser(user);
                    System.out.println("SQLException: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
            }
        } finally {
            DBManager.closeConnection();
        }
    }



    private PersonalData findPersonalDataById(int id) throws SQLException {
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

    private int addPersonalData(PersonalData personalData) throws SQLException {
        int generatedDataId = -1;
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO personaldata (first_name, last_name, number) VALUES (?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, personalData.getFirst_name());
            preparedStatement.setString(2, personalData.getLast_name());
            preparedStatement.setString(3, personalData.getNumber());
            try{
                preparedStatement.executeUpdate();
                try (java.sql.ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedDataId = rs.getInt(1);
                    }
                }
            }catch(SQLException e){
                System.out.println("SQLException: " + e.getMessage());
                throw new SQLException("Error while inserting personal data");
            }
        }finally {
            DBManager.closeConnection();
        }
        return generatedDataId;
    }

    private void updatePersonalData(PersonalData personalData) throws SQLException {
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

    private void deletePersonalData(PersonalData personalData) throws SQLException {
        try(Connection connection = DBManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM personaldata WHERE data_id = ?");
            preparedStatement.setInt(1, personalData.getData_id());
            try {
                preparedStatement.executeUpdate();
            }catch(SQLException e){
                System.out.println("SQLException: " + e.getMessage());
                throw new SQLException("Error while deleting personal data");
            }
        }finally {
            DBManager.closeConnection();
        }
    }
}
