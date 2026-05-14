package dao;

import model.StockOut;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE: dao/StockOutDAO.java
 * ROLE: All database operations for the `stock_out` table.
 *
 * IMPORTANT: recordStockOut() does TWO things atomically:
 *   1. Inserts a row into stock_out
 *   2. Decreases products.stock by the quantity
 *
 * SAFETY: It first checks that the current stock is sufficient.
 * If stock would go negative, the operation is cancelled.
 *
 * UI Usage:
 *   StockOutDAO dao = new StockOutDAO();
 *   StockOut record = new StockOut(productId, quantity, remarks, userId);
 *   boolean ok = dao.recordStockOut(record);
 *   if (!ok) JOptionPane.showMessageDialog(frame, "Insufficient stock!");
 */
public class StockOutDAO {

    // ── RECORD STOCK OUT ──────────────────────────────────────────────
    public boolean recordStockOut(StockOut s) {
        String checkSql  = "SELECT stock FROM products WHERE id = ?";
        String insertSql = "INSERT INTO stock_out (product_id, quantity, remarks, user_id) "
                + "VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE products SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // 1. Check current stock
            int currentStock = 0;
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, s.getProductId());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    currentStock = rs.getInt("stock");
                }
            }

            // 2. Guard: prevent negative stock
            if (currentStock < s.getQuantity()) {
                conn.rollback();
                System.err.println("[StockOutDAO] Insufficient stock. Available: "
                        + currentStock + ", Requested: " + s.getQuantity());
                return false;
            }

            // 3. Insert stock_out record
            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setInt(1, s.getProductId());
                insert.setInt(2, s.getQuantity());
                insert.setString(3, s.getRemarks() != null ? s.getRemarks().trim() : "");
                insert.setInt(4, s.getUserId());
                insert.executeUpdate();
            }

            // 4. Decrease product stock
            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.setInt(1, s.getQuantity());
                update.setInt(2, s.getProductId());
                update.executeUpdate();
            }

            conn.commit();  // All steps succeeded — save
            return true;

        } catch (SQLException e) {
            System.err.println("[StockOutDAO] recordStockOut failed: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
        }
    }

    // ── GET ALL ───────────────────────────────────────────────────────
    public List<StockOut> getAllStockOut() {
        List<StockOut> list = new ArrayList<>();
        String sql = "SELECT so.id, so.product_id, p.name AS product_name, "
                + "so.quantity, so.remarks, so.user_id, so.created_at "
                + "FROM stock_out so "
                + "JOIN products p ON so.product_id = p.id "
                + "ORDER BY so.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new StockOut(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getString("remarks"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("[StockOutDAO] getAllStockOut failed: " + e.getMessage());
        }
        return list;
    }

    // ── GET BY PRODUCT ────────────────────────────────────────────────
    public List<StockOut> getStockOutByProduct(int productId) {
        List<StockOut> list = new ArrayList<>();
        String sql = "SELECT so.id, so.product_id, p.name AS product_name, "
                + "so.quantity, so.remarks, so.user_id, so.created_at "
                + "FROM stock_out so "
                + "JOIN products p ON so.product_id = p.id "
                + "WHERE so.product_id = ? "
                + "ORDER BY so.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new StockOut(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getString("remarks"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("[StockOutDAO] getStockOutByProduct failed: " + e.getMessage());
        }
        return list;
    }
}
