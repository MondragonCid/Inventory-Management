package dao;

import model.Product;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE: dao/ProductDAO.java
 * ROLE: All database operations for the `products` table.
 *
 * OOP — POLYMORPHISM: implements IProductDAO interface.
 * The controller depends on the IProductDAO interface, not this
 * concrete class directly. This allows easy swapping of implementations.
 *
 * UNCHANGED from previous version except: added "implements IProductDAO"
 */
public class ProductDAO implements IProductDAO {

    @Override
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (name, category_id, supplier_id, price, stock, unit) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName().trim());
            stmt.setInt(2, p.getCategoryId());
            stmt.setInt(3, p.getSupplierId());
            stmt.setDouble(4, p.getPrice());
            stmt.setInt(5, p.getStock());
            stmt.setString(6, p.getUnit() != null ? p.getUnit().trim() : "pcs");

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDAO] addProduct failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, category_id, supplier_id, price, stock, unit "
                + "FROM products ORDER BY name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO] getAllProducts failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET name = ?, category_id = ?, supplier_id = ?, "
                + "price = ?, stock = ?, unit = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName().trim());
            stmt.setInt(2, p.getCategoryId());
            stmt.setInt(3, p.getSupplierId());
            stmt.setDouble(4, p.getPrice());
            stmt.setInt(5, p.getStock());
            stmt.setString(6, p.getUnit() != null ? p.getUnit().trim() : "pcs");
            stmt.setInt(7, p.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDAO] updateProduct failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDAO] deleteProduct failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        List<Product> results = new ArrayList<>();
        String sql = "SELECT id, name, category_id, supplier_id, price, stock, unit "
                + "FROM products WHERE name LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword.trim() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO] searchProducts failed: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        List<Product> results = new ArrayList<>();
        String sql = "SELECT id, name, category_id, supplier_id, price, stock, unit "
                + "FROM products WHERE stock <= ? ORDER BY stock ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO] getLowStockProducts failed: " + e.getMessage());
        }
        return results;
    }

    @Override
    public boolean restockProduct(int productId, int quantity) {
        if (quantity <= 0) {
            System.err.println("[ProductDAO] Restock quantity must be positive.");
            return false;
        }
        String sql = "UPDATE products SET stock = stock + ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDAO] restockProduct failed: " + e.getMessage());
            return false;
        }
    }

    // Private helper — maps one ResultSet row to a Product object
    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("category_id"),
                rs.getInt("supplier_id"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("unit")
        );
    }
}
