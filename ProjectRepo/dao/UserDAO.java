package dao;

import model.User;
import util.DBConnection;
import util.InputValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE: dao/UserDAO.java
 * ROLE: All database operations for the `users` table.
 *
 * Used by:
 *   AuthController  → login()
 *   Admin UI panels → add/edit/delete users
 */
public class UserDAO {

    // ── LOGIN ─────────────────────────────────────────────────────────
    /**
     * Checks username and password. Returns the User if valid, null if not.
     *
     * Usage in AuthController:
     *   User u = userDAO.login(usernameField.getText(), passwordField.getText());
     *   if (u != null) { // open main window }
     *   else            { // show "Invalid credentials" }
     */
    public User login(String username, String password) {
        if (InputValidator.isEmpty(username) || InputValidator.isEmpty(password)) {
            return null;
        }
        String sql = "SELECT id, username, password, role, full_name, is_active "
                + "FROM users WHERE username = ? AND password = ? AND is_active = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("full_name"),
                        rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] login failed: " + e.getMessage());
        }
        return null;   // null = invalid credentials
    }

    // ── ADD USER ──────────────────────────────────────────────────────
    public boolean addUser(User u) {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getUsername().trim());
            stmt.setString(2, u.getPassword().trim());
            stmt.setString(3, u.getRole());
            stmt.setString(4, u.getFullName().trim());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO] addUser failed: " + e.getMessage());
            return false;
        }
    }

    // ── GET ALL USERS ─────────────────────────────────────────────────
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role, full_name, is_active "
                + "FROM users ORDER BY full_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("full_name"),
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers failed: " + e.getMessage());
        }
        return users;
    }

    // ── UPDATE USER ───────────────────────────────────────────────────
    public boolean updateUser(User u) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, "
                + "full_name = ?, is_active = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getUsername().trim());
            stmt.setString(2, u.getPassword().trim());
            stmt.setString(3, u.getRole());
            stmt.setString(4, u.getFullName().trim());
            stmt.setBoolean(5, u.isActive());
            stmt.setInt(6, u.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO] updateUser failed: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE USER ───────────────────────────────────────────────────
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteUser failed: " + e.getMessage());
            return false;
        }
    }

    // ── CHECK USERNAME TAKEN ──────────────────────────────────────────
    /**
     * Returns true if the username is already in use.
     * Call this before addUser() to show a friendly error.
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("[UserDAO] isUsernameTaken failed: " + e.getMessage());
            return false;
        }
    }
}
