package controller;

/**
 * FILE: controller/BaseController.java
 * ROLE: Abstract base class shared by all controllers.
 *
 * OOP — INHERITANCE:
 *   AuthController, ProductController, and StockController all extend
 *   this class. They inherit the helper methods defined here without
 *   duplicating them.
 *
 *   Common behavior (returning standard result strings) lives here once.
 *   Specific behavior (login, addProduct, recordStockIn) stays in each
 *   subclass.
 *
 *   This is what inheritance is for: sharing reusable code upward into
 *   a parent class so subclasses don't repeat themselves.
 */
public abstract class BaseController {

    // Standard success signal used by every controller method
    protected static final String SUCCESS = "OK";

    /**
     * Returns a standard success result.
     * All controllers use this so "OK" is never a raw magic string.
     */
    protected String success() {
        return SUCCESS;
    }

    /**
     * Returns a standard failure message.
     * @param context  short description e.g. "save product", "delete category"
     */
    protected String failure(String context) {
        return "Failed to " + context + ". Please try again.";
    }

    /**
     * Returns true if the result string means success.
     * UI code can call this instead of hardcoding .equals("OK").
     *
     *   if (BaseController.isSuccess(result)) { refreshTable(); }
     */
    public static boolean isSuccess(String result) {
        return SUCCESS.equals(result);
    }
}
