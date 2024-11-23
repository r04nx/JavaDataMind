package com.javadata.service;

import com.javadata.data.DatabaseManager;
import com.javadata.model.UserProfile;

import java.sql.*;

public class UserService {
    private final DatabaseManager dbManager;

    public UserService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public void createUser(UserProfile user) throws SQLException {
        String sql = "INSERT INTO users (name, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.executeUpdate();
        }
    }

    public UserProfile authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new UserProfile(
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        }
        return null;
    }
} 