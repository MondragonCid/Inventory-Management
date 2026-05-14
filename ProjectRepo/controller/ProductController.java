package controller;

import dao.IProductDAO;
import dao.ProductDAO;
import model.Product;
import util.InputValidator;

import java.util.List;

/**
 * FILE: controller/ProductController.java
 * ROLE: Business logic and validation for product operations.
 *
 * OOP — INHERITANCE: extends BaseController (inherits success/failure helpers)
 * OOP — ABSTRACTION: UI calls these methods without knowing any SQL
 * OOP — POLYMORPHISM: uses IProductDAO interface, not ProductDAO directly
 *
 * UI Usage:
 *   ProductController pc = new ProductController();
 *   String result = pc.addProduct(name, catId, supId, priceText, stockText, unit);
 *   if (BaseController.isSuccess(result)) { refreshTable(); }
 *   else { JOptionPane.showMessageDialog(frame, result); }
 */
public class ProductController extends BaseController {

    // Uses the interface type — demonstrates polymorphism
    private final IProductDAO productDAO = new ProductDAO();

    // ── ADD ───────────────────────────────────────────────────────────
    public String addProduct(String name, int categoryId, int supplierId,
                             String priceText, String stockText, String unit) {

        String error = validateInputs(name, priceText, stockText);
        if (error != null) return error;

        Product p = new Product(
                name.trim(),
                categoryId,
                supplierId,
                Double.parseDouble(priceText.trim()),
                Integer.parseInt(stockText.trim()),
                unit.trim().isEmpty() ? "pcs" : unit.trim()
        );

        return productDAO.addProduct(p) ? success() : failure("save product");
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    public String updateProduct(int id, String name, int categoryId, int supplierId,
                                String priceText, String stockText, String unit) {

        String error = validateInputs(name, priceText, stockText);
        if (error != null) return error;

        Product p = new Product(
                id,
                name.trim(),
                categoryId,
                supplierId,
                Double.parseDouble(priceText.trim()),
                Integer.parseInt(stockText.trim()),
                unit.trim().isEmpty() ? "pcs" : unit.trim()
        );

        return productDAO.updateProduct(p) ? success() : failure("update product");
    }

    // ── DELETE ────────────────────────────────────────────────────────
    public String deleteProduct(int id) {
        if (id <= 0) return "Invalid product selected.";
        return productDAO.deleteProduct(id) ? success() : failure("delete product");
    }

    // ── GET ALL ───────────────────────────────────────────────────────
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    // ── SEARCH ────────────────────────────────────────────────────────
    public List<Product> searchProducts(String keyword) {
        if (InputValidator.isEmpty(keyword)) return getAllProducts();
        return productDAO.searchProducts(keyword);
    }

    // ── LOW STOCK ─────────────────────────────────────────────────────
    public List<Product> getLowStockProducts(int threshold) {
        return productDAO.getLowStockProducts(threshold);
    }

    // ── RESTOCK ───────────────────────────────────────────────────────
    public String restockProduct(int productId, String quantityText) {
        if (!InputValidator.isPositiveInt(quantityText)) {
            return "Quantity must be a positive whole number.";
        }
        int qty = Integer.parseInt(quantityText.trim());
        return productDAO.restockProduct(productId, qty) ? success() : failure("restock product");
    }

    // ── PRIVATE VALIDATION ────────────────────────────────────────────
    private String validateInputs(String name, String priceText, String stockText) {
        if (InputValidator.isEmpty(name))               return "Product name cannot be empty.";
        if (!InputValidator.isPositiveNumber(priceText)) return "Price must be a positive number (e.g. 185.00).";
        if (!InputValidator.isNonNegativeInt(stockText)) return "Stock must be a whole number of 0 or more.";
        return null;
    }
}
