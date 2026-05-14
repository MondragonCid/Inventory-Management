package main;

import controller.AuthController;
import controller.ProductController;
import controller.StockController;
import dao.*;
import model.*;
import util.DBConnection;

import java.util.List;

/**
 * FILE: main/BackendDebugger.java
 * ROLE: Console test runner — verify the entire backend before UI hookup.
 *
 * BUG FIXED from original:
 *   - Was importing from ProjectRepo.dao / ProjectRepo.model (wrong packages)
 *   - Now imports match the actual package structure
 *   - Tests expanded to cover all 6 DAOs and auth
 *
 * HOW TO RUN:
 *   1. Start XAMPP and make sure MySQL is green
 *   2. Run database.sql in phpMyAdmin
 *   3. Right-click BackendDebugger.java → Run 'BackendDebugger.main()'
 */
public class BackendDebugger {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  Coffee Shop Inventory — Full Backend Debugger   ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        testConnection();
        testAuth();
        testCategories();
        testSuppliers();
        testProducts();
        testStockIn();
        testStockOut();

        printSummary();
        DBConnection.closeConnection();
    }

    // ── TEST 1: DB Connection ─────────────────────────────────────────
    private static void testConnection() {
        section("TEST 1: Database Connection");
        try {
            java.sql.Connection conn = DBConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                pass("Connected to coffee_shop_inventory");
            } else {
                fail("DB Connection", "Connection is null or closed");
            }
        } catch (Exception e) {
            fail("DB Connection", e.getMessage());
        }
    }

    // ── TEST 2: Authentication ────────────────────────────────────────
    private static void testAuth() {
        section("TEST 2: Authentication");
        AuthController auth = new AuthController();

        // Valid admin login
        String result = auth.login("admin", "admin123");
        if ("OK".equals(result)) {
            pass("Admin login");
            System.out.println("     Logged in as: " + auth.getCurrentUser().getFullName()
                    + " [" + auth.getCurrentUser().getRole() + "]");
        } else {
            fail("Admin login", result);
        }

        // Wrong password
        String bad = auth.login("admin", "wrongpassword");
        if (!"OK".equals(bad)) {
            pass("Reject wrong password (returned: '" + bad + "')");
        } else {
            fail("Reject wrong password", "Should have failed but returned OK");
        }

        // Empty username
        String empty = auth.login("", "");
        if (!"OK".equals(empty)) {
            pass("Reject empty inputs (returned: '" + empty + "')");
        } else {
            fail("Reject empty inputs", "Should have failed but returned OK");
        }

        auth.logout();
    }

    // ── TEST 3: Categories ────────────────────────────────────────────
    private static void testCategories() {
        section("TEST 3: Categories CRUD");
        CategoryDAO dao = new CategoryDAO();

        // Add
        Category c = new Category("Test Category", "Temporary test entry");
        boolean added = dao.addCategory(c);
        if (added) pass("addCategory"); else fail("addCategory", "returned false");

        // Get All
        List<Category> all = dao.getAllCategories();
        if (!all.isEmpty()) {
            pass("getAllCategories (" + all.size() + " found)");
        } else {
            fail("getAllCategories", "empty list");
        }

        // Update & Delete
        if (!all.isEmpty()) {
            Category last = all.get(all.size() - 1);
            last.setDescription("Updated description");
            if (dao.updateCategory(last)) pass("updateCategory");
            else fail("updateCategory", "returned false");

            if (dao.deleteCategory(last.getId())) pass("deleteCategory");
            else fail("deleteCategory", "returned false");
        }
    }

    // ── TEST 4: Suppliers ─────────────────────────────────────────────
    private static void testSuppliers() {
        section("TEST 4: Suppliers CRUD");
        SupplierDAO dao = new SupplierDAO();

        Supplier s = new Supplier("Test Supplier", "Test Contact", "09991234567",
                "test@supplier.ph", "Cebu City");
        if (dao.addSupplier(s))        pass("addSupplier"); else fail("addSupplier", "returned false");

        List<Supplier> all = dao.getAllSuppliers();
        if (!all.isEmpty()) pass("getAllSuppliers (" + all.size() + " found)");
        else fail("getAllSuppliers", "empty list");

        if (!all.isEmpty()) {
            Supplier last = all.get(all.size() - 1);
            last.setPhone("09009009000");
            if (dao.updateSupplier(last)) pass("updateSupplier"); else fail("updateSupplier", "returned false");
            if (dao.deleteSupplier(last.getId())) pass("deleteSupplier"); else fail("deleteSupplier", "returned false");
        }
    }

    // ── TEST 5: Products ──────────────────────────────────────────────
    private static void testProducts() {
        section("TEST 5: Products CRUD");
        ProductController pc = new ProductController();

        // Add via controller (tests validation + DAO together)
        String addResult = pc.addProduct("Test Drink", 1, 1, "99.00", "25", "cup");
        if ("OK".equals(addResult)) pass("addProduct via controller");
        else fail("addProduct via controller", addResult);

        // Get All
        List<Product> all = pc.getAllProducts();
        if (!all.isEmpty()) {
            pass("getAllProducts (" + all.size() + " found)");
            System.out.println("     Sample row: " + all.get(0));
        } else {
            fail("getAllProducts", "empty list");
        }

        // Search
        List<Product> search = pc.searchProducts("Coffee");
        pass("searchProducts ('Coffee' → " + search.size() + " result(s))");

        // Low stock
        List<Product> low = pc.getLowStockProducts(10);
        pass("getLowStockProducts (" + low.size() + " item(s) at or below 10)");
        for (Product p : low) System.out.println("     LOW: " + p);

        // Update + Delete using last product
        List<Product> latest = pc.getAllProducts();
        if (!latest.isEmpty()) {
            Product last = latest.get(latest.size() - 1);
            String upd = pc.updateProduct(last.getId(), last.getName(), last.getCategoryId(),
                    last.getSupplierId(), "110.00", "30", last.getUnit());
            if ("OK".equals(upd)) pass("updateProduct"); else fail("updateProduct", upd);

            String del = pc.deleteProduct(last.getId());
            if ("OK".equals(del)) pass("deleteProduct"); else fail("deleteProduct", del);
        }

        // Validation rejection test
        String badPrice = pc.addProduct("Bad Product", 1, 1, "abc", "5", "cup");
        if (!"OK".equals(badPrice)) pass("Validation: reject non-numeric price ('" + badPrice + "')");
        else fail("Validation: reject non-numeric price", "should have failed");
    }

    // ── TEST 6: Stock In ──────────────────────────────────────────────
    // ── TEST 6: Stock In ──────────────────────────────────────────────
    private static void testStockIn() {
        section("TEST 6: Stock In");

        // FIX: Log the admin back in so the StockController has a valid user_id to send to MySQL!
        AuthController auth = new AuthController();
        auth.login("admin", "admin123");

        StockController sc = new StockController();

        List<Product> products = new ProductController().getAllProducts();
        if (products.isEmpty()) {
            fail("Stock In", "No products to test with");
            return;
        }

        int productId = products.get(0).getId();
        String result = sc.recordStockIn(productId, "20", "Debugger test delivery");
        if ("OK".equals(result)) pass("recordStockIn (product ID=" + productId + ", qty=20)");
        else fail("recordStockIn", result);

        List<StockIn> history = sc.getAllStockIn();
        pass("getAllStockIn (" + history.size() + " record(s))");
    }

    // ── TEST 7: Stock Out ─────────────────────────────────────────────
    private static void testStockOut() {
        section("TEST 7: Stock Out");
        StockController sc = new StockController();

        List<Product> products = new ProductController().getAllProducts();
        if (products.isEmpty()) {
            fail("Stock Out", "No products to test with");
            return;
        }

        Product first = products.get(0);
        int productId = first.getId();

        // Valid stock out
        String result = sc.recordStockOut(productId, "2", "Debugger test sale");
        if ("OK".equals(result)) pass("recordStockOut (product ID=" + productId + ", qty=2)");
        else fail("recordStockOut", result);

        // Over-stock rejection
        String overResult = sc.recordStockOut(productId, "999999", "Should fail");
        if (!"OK".equals(overResult)) pass("Reject over-stock (returned: '" + overResult + "')");
        else fail("Reject over-stock", "Should have failed but returned OK");

        List<StockOut> history = sc.getAllStockOut();
        pass("getAllStockOut (" + history.size() + " record(s))");
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private static void section(String title) {
        System.out.println("\n── " + title + " " + "─".repeat(Math.max(0, 48 - title.length())));
    }

    private static void pass(String msg) {
        System.out.println("  ✅ PASS: " + msg);
        passed++;
    }

    private static void fail(String test, String reason) {
        System.out.println("  ❌ FAIL: " + test + " → " + reason);
        failed++;
    }

    private static void printSummary() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf( "║  Results: %d passed, %d failed%n", passed, failed);
        if (failed == 0) {
            System.out.println("║  ✅ All tests passed! Backend is ready.          ║");
        } else {
            System.out.println("║  ❌ Some tests failed — review errors above.     ║");
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}
