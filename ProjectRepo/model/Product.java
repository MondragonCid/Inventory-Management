package model;

/**
 * FILE: model/Product.java
 * ROLE: Represents one row in the `products` table.
 *
 * OOP — Encapsulation: all fields private, accessed via getters/setters.
 *
 * CHANGES from original:
 *   - Added categoryId and supplierId foreign key fields
 *   - Added unit field (cup, pcs, bottle, pack)
 *   - Added full 6-field constructor for DB loading
 *   - Kept original 3-field constructor for simple usage
 */
public class Product {

    private int    id;
    private String name;
    private int    categoryId;  // FK → categories.id (0 = no category)
    private int    supplierId;  // FK → suppliers.id  (0 = no supplier)
    private double price;
    private int    stock;
    private String unit;        // "cup", "pcs", "bottle", "pack"

    // ── Constructor: for NEW products (ID auto-assigned by MySQL) ────
    public Product(String name, int categoryId, int supplierId,
                   double price, int stock, String unit) {
        this.name       = name;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.price      = price;
        this.stock      = stock;
        this.unit       = unit;
    }

    // ── Constructor: for products LOADED from the database ───────────
    public Product(int id, String name, int categoryId, int supplierId,
                   double price, int stock, String unit) {
        this.id         = id;
        this.name       = name;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.price      = price;
        this.stock      = stock;
        this.unit       = unit;
    }

    // ── Getters ───────────────────────────────────────────────────────
    public int    getId()         { return id; }
    public String getName()       { return name; }
    public int    getCategoryId() { return categoryId; }
    public int    getSupplierId() { return supplierId; }
    public double getPrice()      { return price; }
    public int    getStock()      { return stock; }
    public String getUnit()       { return unit; }

    // ── Setters ───────────────────────────────────────────────────────
    public void setId(int id)                 { this.id         = id; }
    public void setName(String name)          { this.name       = name; }
    public void setCategoryId(int cid)        { this.categoryId = cid; }
    public void setSupplierId(int sid)        { this.supplierId = sid; }
    public void setPrice(double price)        { this.price      = price; }
    public void setStock(int stock)           { this.stock      = stock; }
    public void setUnit(String unit)          { this.unit       = unit; }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.2f, stock=%d %s}",
                id, name, price, stock, unit);
    }
}
