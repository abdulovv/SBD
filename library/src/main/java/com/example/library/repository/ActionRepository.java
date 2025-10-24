package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Action;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ActionRepository {
    public Action findActionById(int id) throws SQLException {
        Action action = null;
        try (Connection connection = DBManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM actions WHERE action_id = ?");
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Integer action_id = resultSet.getInt("action_id");
                    String action_name = resultSet.getString("description");
                    if (action_id != null && action_id != null) {
                        action = new Action();
                        action.setAction_id(action_id);
                        action.setDescription(action_name);
                    }
                }
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
        return action;
    }

    public void addAction(Action action) throws SQLException {
        try (Connection connection = DBManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Actions (description) VALUES (?)");
            preparedStatement.setString(1, action.getDescription());
            try{
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void updateAction(Action action) throws SQLException {
        try (Connection connection = DBManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE actions SET description = ? WHERE action_id = ?");
            preparedStatement.setString(1, action.getDescription());
            preparedStatement.setInt(2, action.getAction_id());
            try{
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }

    public void deleteAction(Action action) throws SQLException {
        try (Connection connection = DBManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM actions WHERE action_id = ?");
            preparedStatement.setInt(1, action.getAction_id());
            try{
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                System.out.println("SQLException: " + e.getMessage());
            }
        }finally {
            DBManager.closeConnection();
        }
    }
}
