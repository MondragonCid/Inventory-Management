package dao;

import model.Product;
import java.util.List;

/**
 * FILE: dao/IProductDAO.java
 * ROLE: Interface that defines the contract for product data access.
 *
 * OOP — POLYMORPHISM:
 *   This interface demonstrates polymorphism. Any class that implements
 *   IProductDAO must provide all of these methods.
 *
 *   ProductDAO implements this interface for real MySQL operations.
 *   In a test environment, a MockProductDAO could also implement it
 *   without touching the database at all.
 *
 *   The controller uses IProductDAO as its type, so it works with
 *   ANY implementation without changing its code:
 *
 *     IProductDAO dao = new ProductDAO();     // real DB
 *     IProductDAO dao = new MockProductDAO(); // for testing
 *
 * This is the core idea of polymorphism: same interface, different behavior.
 */
public interface IProductDAO {

    boolean addProduct(Product p);

    List<Product> getAllProducts();

    boolean updateProduct(Product p);

    boolean deleteProduct(int id);

    List<Product> searchProducts(String keyword);

    List<Product> getLowStockProducts(int threshold);

    boolean restockProduct(int productId, int quantity);
}
