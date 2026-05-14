package model;

import java.time.LocalDateTime;

/**
 * FILE: model/StockOut.java
 * ROLE: Represents one row in the `stock_out` table.
 *
 * OOP — Encapsulation: all fields private, accessed via getters/setters.
 */
public class StockOut {

    private int           id;
    private int           productId;
    private String        productName;    // Joined from products — display only
    private int           quantity;
    private String        remarks;
    private int           userId;
    private LocalDateTime createdAt;

    // ── Constructor: for NEW stock_out records ───────────────────────
    public StockOut(int productId, int quantity, String remarks, int userId) {
        this.productId = productId;
        this.quantity  = quantity;
        this.remarks   = remarks;
        this.userId    = userId;
    }

    // ── Constructor: for records LOADED from the database ────────────
    public StockOut(int id, int productId, String productName,
                    int quantity, String remarks, int userId, LocalDateTime createdAt) {
        this.id          = id;
        this.productId   = productId;
        this.productName = productName;
        this.quantity    = quantity;
        this.remarks     = remarks;
        this.userId      = userId;
        this.createdAt   = createdAt;
    }

    // ── Getters ───────────────────────────────────────────────────────
    public int           getId()          { return id; }
    public int           getProductId()   { return productId; }
    public String        getProductName() { return productName; }
    public int           getQuantity()    { return quantity; }
    public String        getRemarks()     { return remarks; }
    public int           getUserId()      { return userId; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────
    public void setId(int id)                      { this.id          = id; }
    public void setProductId(int productId)        { this.productId   = productId; }
    public void setProductName(String name)        { this.productName = name; }
    public void setQuantity(int quantity)          { this.quantity    = quantity; }
    public void setRemarks(String remarks)         { this.remarks     = remarks; }
    public void setUserId(int userId)              { this.userId      = userId; }
    public void setCreatedAt(LocalDateTime dt)     { this.createdAt   = dt; }

    @Override
    public String toString() {
        return String.format("StockOut{id=%d, product='%s', qty=%d, date=%s}",
                id, productName, quantity, createdAt);
    }
}
