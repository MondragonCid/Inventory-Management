package dao;

import model.Category;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE: dao/CategoryDAO.java
 * ROLE: All database operations for the `categories` table.
 *
 * UI Usage:
 *   CategoryDAO dao = new CategoryDAO();
 *   List<Category> list = dao.getAllCategories();
 *   // populate JComboBox:
 *   for (Category c : list) { categoryCombo.addItem(c); }
 *   // get selected:
 *   Category selected = (Category) categoryCombo.getSelectedItem();
 *   int categoryId = selected.getId();
 */
public class CategoryDAO {

    // ── ADD ───────────────────────────────────────────────────────────
    public boolean addCategory(Category c) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getName().trim());
            stmt.setString(2, c.getDescription() != null ? c.getDescription().trim() : "");

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[CategoryDAO] addCategory failed: " + e.getMessage());
            return false;
        }
    }

    // ── GET ALL ───────────────────────────────────────────────────────
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name, description FROM categories ORDER BY name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] getAllCategories failed: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    public boolean updateCategory(Category c) {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getName().trim());
            stmt.setString(2, c.getDescription() != null ? c.getDescription().trim() : "");
            stmt.setInt(3, c.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[CategoryDAO] updateCategory failed: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────
    /**
     * Deletes a category. Products with this category will have
     * category_id set to NULL (ON DELETE SET NULL in schema).
     */
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[CategoryDAO] deleteCategory failed: " + e.getMessage());
            return false;
        }
    }
}
