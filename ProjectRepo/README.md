<img width="765" height="615" alt="classDiagram" src="https://github.com/user-attachments/assets/26568a9d-9878-45c8-a215-4f3ec8ac9005" />
<img width="445" height="685" alt="useCaseDiagram" src="https://github.com/user-attachments/assets/88895c89-2fe9-41b3-87a1-1161aecf3e1b" />

# Coffee Shop Inventory Management System
## Backend Integration Guide (CSIT 213 — OOP 2)

---

## Project Structure

```
ProjectRepo/
├── model/
│   └── Product.java          ← Data object (one row = one Product)
├── util/
│   └── DBConnection.java     ← Singleton DB connection manager
├── dao/
│   └── ProductDAO.java       ← ALL SQL lives here (CRUD + extras)
├── main/
│   └── BackendDebugger.java  ← Run this first to verify everything works
└── authenticator/
    ├── User.java             ← (your existing shell)
    └── SessionManager.java   ← (your existing shell)
```

---

## Step 1 — Database Setup (phpMyAdmin)

1. Open **XAMPP Control Panel** → start **Apache** and **MySQL**
2. Go to `http://localhost/phpmyadmin`
3. Click the **SQL** tab
4. Paste and run `coffee_shop_db_setup.sql`
5. Verify the `products` table appears under `coffee_shop_db`

---

## Step 2 — IntelliJ Setup

1. Copy the 4 Java files into your `ProjectRepo/` source folders
2. Confirm `mysql-connector-j-9.7.0.jar` is in `/lib`
3. File → Project Structure → Modules → Dependencies → confirm the JAR is listed
4. Right-click `BackendDebugger.java` → **Run**

---

## Step 3 — Vincent's UI Integration Cheat Sheet

```java
// At the top of your JFrame class:
ProductDAO dao = new ProductDAO();

// ── Load all products into a JTable ──────────────────────────────────
List<Product> products = dao.getAllProducts();
DefaultTableModel model = (DefaultTableModel) table.getModel();
model.setRowCount(0); // clear first
for (Product p : products) {
    model.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock()});
}

// ── Add button action ─────────────────────────────────────────────────
Product newP = new Product(nameField.getText(),
    Double.parseDouble(priceField.getText()),
    Integer.parseInt(stockField.getText()));
if (dao.addProduct(newP)) {
    JOptionPane.showMessageDialog(this, "Product added!");
    refreshTable(); // reload getAllProducts()
}

// ── Edit button action ────────────────────────────────────────────────
int selectedRow = table.getSelectedRow();
int id = (int) table.getValueAt(selectedRow, 0);
Product updated = new Product(id, nameField.getText(),
    Double.parseDouble(priceField.getText()),
    Integer.parseInt(stockField.getText()));
dao.updateProduct(updated);
refreshTable();

// ── Delete button action ──────────────────────────────────────────────
int selectedId = (int) table.getValueAt(table.getSelectedRow(), 0);
int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?");
if (confirm == JOptionPane.YES_OPTION) {
    dao.deleteProduct(selectedId);
    refreshTable();
}

// ── Search bar (DocumentListener) ─────────────────────────────────────
List<Product> results = dao.searchProducts(searchField.getText());
// populate table with results...

// ── Low stock alert on startup ─────────────────────────────────────────
List<Product> lowStock = dao.getLowStockProducts(10);
if (!lowStock.isEmpty()) {
    JOptionPane.showMessageDialog(this,
        "⚠ " + lowStock.size() + " item(s) are running low on stock!",
        "Restock Alert", JOptionPane.WARNING_MESSAGE);
}

// ── On app close ─────────────────────────────────────────────────────
// In your JFrame constructor:
addWindowListener(new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent e) {
        DBConnection.closeConnection();
    }
});
```

---

## Useful phpMyAdmin Queries

```sql
-- View everything
SELECT * FROM products;

-- View sorted by stock (lowest first — quick restock check)
SELECT * FROM products ORDER BY stock ASC;

-- Find low-stock items
SELECT * FROM products WHERE stock <= 10;

-- Find a product by name
SELECT * FROM products WHERE name LIKE '%Macchiato%';

-- Manually restock a product (replace 1 with actual id)
UPDATE products SET stock = stock + 20 WHERE id = 1;

-- Reset and re-seed the table (useful during testing)
TRUNCATE TABLE products;
INSERT INTO products (name, price, stock) VALUES
    ('Caramel Macchiato', 185.00, 50),
    ('Brewed Coffee',      95.00, 100),
    ('Iced Americano',    130.00, 75);

-- Check how many products you have
SELECT COUNT(*) AS total_products FROM products;
```

---

## Recommendations for Future Additions (Keep It Simple)

| Feature | What to Add | Effort |
|---|---|---|
| **Sales Logging** | `sales` table: `id, product_id, qty_sold, sale_date` | Low |
| **User Login** | `users` table + `SessionManager.java` | Low |
| **Category Tags** | Add a `category VARCHAR(50)` column to `products` | Very Low |
| **Export to CSV** | Loop `getAllProducts()` and write with `FileWriter` | Low |
| **Stock History** | `inventory_log` table tracks every stock change | Medium |
