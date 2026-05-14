package dao;

import model.Supplier;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE: dao/SupplierDAO.java
 * ROLE: All database operations for the `suppliers` table.
 *
 * UI Usage:
 *   SupplierDAO dao = new SupplierDAO();
 *   List<Supplier> list = dao.getAllSuppliers();
 *   // populate JComboBox:
 *   for (Supplier s : list) { supplierCombo.addItem(s); }
 */
public class SupplierDAO {

    // ── ADD ───────────────────────────────────────────────────────────
    public boolean addSupplier(Supplier s) {
        String sql = "INSERT INTO suppliers (name, contact_name, phone, email, address) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getName().trim());
            stmt.setString(2, s.getContactName() != null ? s.getContactName().trim() : "");
            stmt.setString(3, s.getPhone()       != null ? s.getPhone().trim()       : "");
            stmt.setString(4, s.getEmail()       != null ? s.getEmail().trim()       : "");
            stmt.setString(5, s.getAddress()     != null ? s.getAddress().trim()     : "");

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[SupplierDAO] addSupplier failed: " + e.getMessage());
            return false;
        }
    }

    // ── GET ALL ───────────────────────────────────────────────────────
    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT id, name, contact_name, phone, email, address "
                + "FROM suppliers ORDER BY name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[SupplierDAO] getAllSuppliers failed: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    public boolean updateSupplier(Supplier s) {
        String sql = "UPDATE suppliers SET name = ?, contact_name = ?, "
                + "phone = ?, email = ?, address = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getName().trim());
            stmt.setString(2, s.getContactName() != null ? s.getContactName().trim() : "");
            stmt.setString(3, s.getPhone()       != null ? s.getPhone().trim()       : "");
            stmt.setString(4, s.getEmail()       != null ? s.getEmail().trim()       : "");
            stmt.setString(5, s.getAddress()     != null ? s.getAddress().trim()     : "");
            stmt.setInt(6, s.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[SupplierDAO] updateSupplier failed: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────
    public boolean deleteSupplier(int id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[SupplierDAO] deleteSupplier failed: " + e.getMessage());
            return false;
        }
    }
}
