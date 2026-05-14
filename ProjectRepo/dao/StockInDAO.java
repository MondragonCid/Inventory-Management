package dao;

import model.StockIn;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE: dao/StockInDAO.java
 * ROLE: All database operations for the `stock_in` table.
 *
 * IMPORTANT: recordStockIn() does TWO things atomically:
 *   1. Inserts a row into stock_in
 *   2. Increases products.stock by the same quantity
 * Both succeed or both are rolled back. This keeps inventory accurate.
 *
 * UI Usage:
 *   StockInDAO dao = new StockInDAO();
 *   StockIn record = new StockIn(productId, quantity, remarks, userId);
 *   boolean ok = dao.recordStockIn(record);
 */
public class StockInDAO {

    // ── RECORD STOCK IN ───────────────────────────────────────────────
    /**
     * Inserts a stock_in row AND increases the product's stock count.
     * Uses a transaction so both changes happen together or not at all.
     */
    public boolean recordStockIn(StockIn s) {
        String insertSql = "INSERT INTO stock_in (product_id, quantity, remarks, user_id) "
                + "VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE products SET stock = stock + ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // 1. Insert stock_in record
            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setInt(1, s.getProductId());
                insert.setInt(2, s.getQuantity());
                insert.setString(3, s.getRemarks() != null ? s.getRemarks().trim() : "");
                insert.setInt(4, s.getUserId());
                insert.executeUpdate();
            }

            // 2. Increase product stock
            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.setInt(1, s.getQuantity());
                update.setInt(2, s.getProductId());
                update.executeUpdate();
            }

            conn.commit();  // Both succeeded — save both changes
            return true;

        } catch (SQLException e) {
            System.err.println("[StockInDAO] recordStockIn failed: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
        }
    }

    // ── GET ALL ───────────────────────────────────────────────────────
    /**
     * Returns all stock_in records with the product name joined in.
     * UI: use for a "Stock In History" table.
     */
    public List<StockIn> getAllStockIn() {
        List<StockIn> list = new ArrayList<>();
        String sql = "SELECT si.id, si.product_id, p.name AS product_name, "
                + "si.quantity, si.remarks, si.user_id, si.created_at "
                + "FROM stock_in si "
                + "JOIN products p ON si.product_id = p.id "
                + "ORDER BY si.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new StockIn(
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
            System.err.println("[StockInDAO] getAllStockIn failed: " + e.getMessage());
        }
        return list;
    }

    // ── GET BY PRODUCT ────────────────────────────────────────────────
    /**
     * Returns all stock_in records for one specific product.
     * UI: show history for a selected product.
     */
    public List<StockIn> getStockInByProduct(int productId) {
        List<StockIn> list = new ArrayList<>();
        String sql = "SELECT si.id, si.product_id, p.name AS product_name, "
                + "si.quantity, si.remarks, si.user_id, si.created_at "
                + "FROM stock_in si "
                + "JOIN products p ON si.product_id = p.id "
                + "WHERE si.product_id = ? "
                + "ORDER BY si.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new StockIn(
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
            System.err.println("[StockInDAO] getStockInByProduct failed: " + e.getMessage());
        }
        return list;
    }
}
