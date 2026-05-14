package controller;

import authenticator.SessionManager;
import dao.StockInDAO;
import dao.StockOutDAO;
import model.StockIn;
import model.StockOut;
import util.InputValidator;

import java.util.List;

/**
 * FILE: controller/StockController.java
 * ROLE: Business logic for Stock In and Stock Out operations.
 *
 * OOP — INHERITANCE: extends BaseController
 * OOP — ABSTRACTION: UI never touches StockInDAO or StockOutDAO directly.
 */
public class StockController extends BaseController {

    private final StockInDAO  stockInDAO  = new StockInDAO();
    private final StockOutDAO stockOutDAO = new StockOutDAO();

    /**
     * Records a stock-in event and increases product stock.
     * @return "OK" on success, or a user-friendly error message.
     */
    public String recordStockIn(int productId, String quantityText, String remarks) {
        if (productId <= 0)                              return "Please select a product.";
        if (!InputValidator.isPositiveInt(quantityText)) return "Quantity must be a positive whole number.";

        int qty    = Integer.parseInt(quantityText.trim());
        int userId = getCurrentUserId();

        StockIn record = new StockIn(productId, qty, remarks, userId);
        return stockInDAO.recordStockIn(record) ? success() : failure("record stock in");
    }

    /**
     * Records a stock-out event and decreases product stock.
     * Returns an error if there is insufficient stock.
     * @return "OK" on success, or a user-friendly error message.
     */
    public String recordStockOut(int productId, String quantityText, String remarks) {
        if (productId <= 0)                              return "Please select a product.";
        if (!InputValidator.isPositiveInt(quantityText)) return "Quantity must be a positive whole number.";

        int qty    = Integer.parseInt(quantityText.trim());
        int userId = getCurrentUserId();

        StockOut record = new StockOut(productId, qty, remarks, userId);
        boolean ok = stockOutDAO.recordStockOut(record);
        return ok ? success() : "Insufficient stock or database error. Check the quantity.";
    }

    public List<StockIn>  getAllStockIn()  { return stockInDAO.getAllStockIn(); }
    public List<StockOut> getAllStockOut() { return stockOutDAO.getAllStockOut(); }

    public List<StockIn>  getStockInByProduct(int pid)  { return stockInDAO.getStockInByProduct(pid); }
    public List<StockOut> getStockOutByProduct(int pid) { return stockOutDAO.getStockOutByProduct(pid); }

    private int getCurrentUserId() {
        if (SessionManager.getInstance().isLoggedIn()) {
            return SessionManager.getInstance().getCurrentUser().getId();
        }
        return 0;
    }
}
