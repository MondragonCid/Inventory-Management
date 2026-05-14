package main;

import authenticator.SessionManager;
import controller.AuthController;
import controller.BaseController;
import controller.ProductController;
import controller.StockController;
import dao.CategoryDAO;
import dao.SupplierDAO;
import model.*;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * FILE: main/SimpleUI.java
 * ROLE: Main application window with 5 tabs, all connected to the backend.
 *
 * TABS:
 *   1. Products   — full CRUD + search + low-stock alert on startup
 *   2. Categories — full CRUD
 *   3. Suppliers  — full CRUD
 *   4. Stock In   — record stock in + history table
 *   5. Stock Out  — record stock out + history table
 *
 * HOW TO RUN:
 *   Right-click SimpleUI.java → Run 'SimpleUI.main()'
 *   A login dialog appears first. Use: admin / admin123
 */
public class SimpleUI extends JFrame {

    // ── Controllers & DAOs ────────────────────────────────────────────
    private final ProductController productController = new ProductController();
    private final StockController   stockController   = new StockController();
    private final CategoryDAO       categoryDAO       = new CategoryDAO();
    private final SupplierDAO       supplierDAO       = new SupplierDAO();

    // ── Product tab fields ────────────────────────────────────────────
    private JTable          productTable;
    private DefaultTableModel productTableModel;
    private JTextField      pNameField, pPriceField, pStockField, pUnitField, pSearchField;
    private JComboBox<Category>  pCategoryCombo;
    private JComboBox<Supplier>  pSupplierCombo;
    private JButton         pAddBtn, pEditBtn, pDeleteBtn, pClearBtn;
    private int             editingProductId = -1;   // -1 = adding new, >0 = editing

    // ── Category tab fields ───────────────────────────────────────────
    private JTable          catTable;
    private DefaultTableModel catTableModel;
    private JTextField      catNameField, catDescField;
    private int             editingCatId = -1;

    // ── Supplier tab fields ───────────────────────────────────────────
    private JTable          supTable;
    private DefaultTableModel supTableModel;
    private JTextField      supNameField, supContactField, supPhoneField,
                            supEmailField, supAddressField;
    private int             editingSupId = -1;

    // ── Stock In tab fields ───────────────────────────────────────────
    private JTable          siTable;
    private DefaultTableModel siTableModel;
    private JComboBox<Product> siProductCombo;
    private JTextField      siQtyField, siRemarksField;

    // ── Stock Out tab fields ──────────────────────────────────────────
    private JTable          soTable;
    private DefaultTableModel soTableModel;
    private JComboBox<Product> soProductCombo;
    private JTextField      soQtyField;
    private JComboBox<String>  soReasonsCombo;

    // ─────────────────────────────────────────────────────────────────
    public SimpleUI(String loggedInUser) {
        setTitle("Coffee Shop Inventory — " + loggedInUser);
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Close connection cleanly when window is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DBConnection.closeConnection();
                System.exit(0);
            }
        });

        //  Build tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Products",   buildProductPanel());
        tabs.addTab("Categories", buildCategoryPanel());
        tabs.addTab("Suppliers",  buildSupplierPanel());
        tabs.addTab("Stock In",   buildStockInPanel());
        tabs.addTab("Stock Out",  buildStockOutPanel());
        add(tabs, BorderLayout.CENTER);

        setVisible(true);


        refreshProductTable();
        refreshCategoryTable();
        refreshSupplierTable();
        refreshStockInTable();
        refreshStockOutTable();
        loadProductCombos();   // populates SI/SO product dropdowns

        //  Low stock alert on startup
        checkLowStock();
    }

    // ═════════════════════════════════════════════════════════════════
    // TAB 1: PRODUCTS
    // ═════════════════════════════════════════════════════════════════
    private JPanel buildProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Table ─────────────────────────────────────────────────────
        productTableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Category", "Supplier", "Price", "Stock", "Unit"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getColumnModel().getColumn(0).setMaxWidth(50);

        // ── Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Name, Category
        gbc.gridy = 0;
        addFormRow(formPanel, gbc, 0, "Name:", pNameField = new JTextField(15));
        pCategoryCombo = new JComboBox<>();
        addFormRow(formPanel, gbc, 2, "Category:", pCategoryCombo);

        // Row 1: Supplier, Price
        gbc.gridy = 1;
        pSupplierCombo = new JComboBox<>();
        addFormRow(formPanel, gbc, 0, "Supplier:", pSupplierCombo);
        addFormRow(formPanel, gbc, 2, "Price (₱):", pPriceField = new JTextField(8));

        // Row 2: Stock, Unit
        gbc.gridy = 2;
        addFormRow(formPanel, gbc, 0, "Stock:", pStockField = new JTextField(8));
        pUnitField = new JTextField("pcs", 8);
        addFormRow(formPanel, gbc, 2, "Unit:", pUnitField);

        // Load categories and suppliers into combos
        loadCategoryCombos();
        loadSupplierCombos();

        //  Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pAddBtn    = new JButton("Add Product");
        pEditBtn   = new JButton("Edit Selected");
        pDeleteBtn = new JButton("Delete");
        pClearBtn  = new JButton("Clear");
        JButton refreshBtn = new JButton("Refresh");

        btnPanel.add(pAddBtn);
        btnPanel.add(pEditBtn);
        btnPanel.add(pDeleteBtn);
        btnPanel.add(pClearBtn);
        btnPanel.add(refreshBtn);

        //  Search
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        pSearchField = new JTextField();
        searchPanel.add(pSearchField, BorderLayout.CENTER);

        // ── Assemble ──────────────────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        JPanel bottomForm = new JPanel(new BorderLayout());
        bottomForm.add(btnPanel, BorderLayout.NORTH);
        bottomForm.add(searchPanel, BorderLayout.SOUTH);
        topPanel.add(bottomForm, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        // ── Wire actions ──────────────────────────────────────────────
        pAddBtn.addActionListener(e -> saveProduct());
        pEditBtn.addActionListener(e -> loadProductForEdit());
        pDeleteBtn.addActionListener(e -> deleteProduct());
        pClearBtn.addActionListener(e -> clearProductForm());
        refreshBtn.addActionListener(e -> refreshProductTable());
        pSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { searchProducts(); }
        });

        return panel;
    }

    private void saveProduct() {
        String name      = pNameField.getText().trim();
        String priceText = pPriceField.getText().trim();
        String stockText = pStockField.getText().trim();
        String unit      = pUnitField.getText().trim();

        if (pCategoryCombo.getSelectedItem() == null || pSupplierCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Category or Supplier list is empty. Refresh and try again.");
            return;
        }

        Category selectedCat = (Category) pCategoryCombo.getSelectedItem();
        Supplier selectedSup = (Supplier) pSupplierCombo.getSelectedItem();
        int categoryId = selectedCat.getId();
        int supplierId = selectedSup.getId();

        String result;
        if (editingProductId > 0) {
            // UPDATE mode
            result = productController.updateProduct(
                editingProductId, name, categoryId, supplierId, priceText, stockText, unit);
        } else {
            // ADD mode
            result = productController.addProduct(name, categoryId, supplierId, priceText, stockText, unit);
        }

        if (BaseController.isSuccess(result)) {
            String msg = editingProductId > 0 ? "Product updated!" : "Product added!";
            JOptionPane.showMessageDialog(this, msg);
            refreshProductTable();
            clearProductForm();
            loadProductCombos();  // refresh SI/SO combos too
        } else {
            JOptionPane.showMessageDialog(this, result, "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadProductForEdit() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product row to edit.");
            return;
        }

        int id          = (int)    productTable.getValueAt(selectedRow, 0);
        String name     = (String) productTable.getValueAt(selectedRow, 1);
        double price    = (double) productTable.getValueAt(selectedRow, 4);
        int stock       = (int)    productTable.getValueAt(selectedRow, 5);
        String unit     = (String) productTable.getValueAt(selectedRow, 6);

        pNameField.setText(name);
        pPriceField.setText(String.valueOf(price));
        pStockField.setText(String.valueOf(stock));
        pUnitField.setText(unit);

        // Match combo selection by the displayed name stored in the table
        String catName = (String) productTable.getValueAt(selectedRow, 2);
        String supName = (String) productTable.getValueAt(selectedRow, 3);
        selectComboByName(pCategoryCombo, catName);
        selectComboByName(pSupplierCombo, supName);

        editingProductId = id;
        pAddBtn.setText("Save Changes");
        pEditBtn.setEnabled(false);
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        int id   = (int)    productTable.getValueAt(selectedRow, 0);
        String n = (String) productTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete product: " + n + " (ID=" + id + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = productController.deleteProduct(id);
            if (BaseController.isSuccess(result)) {
                JOptionPane.showMessageDialog(this, "Product deleted.");
                refreshProductTable();
                loadProductCombos();
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchProducts() {
        String keyword = pSearchField.getText().trim();
        List<Product> results = productController.searchProducts(keyword);
        populateProductTable(results);
    }

    private void refreshProductTable() {
        populateProductTable(productController.getAllProducts());
    }

    private void populateProductTable(List<Product> products) {
        productTableModel.setRowCount(0);
        // Build name lookup maps for display
        List<Category> cats = categoryDAO.getAllCategories();
        List<Supplier> sups = supplierDAO.getAllSuppliers();

        for (Product p : products) {
            String catName = cats.stream()
                .filter(c -> c.getId() == p.getCategoryId())
                .map(Category::getName).findFirst().orElse("ID:" + p.getCategoryId());
            String supName = sups.stream()
                .filter(s -> s.getId() == p.getSupplierId())
                .map(Supplier::getName).findFirst().orElse("ID:" + p.getSupplierId());

            productTableModel.addRow(new Object[]{
                p.getId(), p.getName(), catName, supName,
                p.getPrice(), p.getStock(), p.getUnit()
            });
        }
    }

    private void clearProductForm() {
        pNameField.setText("");
        pPriceField.setText("");
        pStockField.setText("");
        pUnitField.setText("pcs");
        if (pCategoryCombo.getItemCount() > 0) pCategoryCombo.setSelectedIndex(0);
        if (pSupplierCombo.getItemCount() > 0) pSupplierCombo.setSelectedIndex(0);
        editingProductId = -1;
        pAddBtn.setText("Add Product");
        pEditBtn.setEnabled(true);
        productTable.clearSelection();
    }

    private void checkLowStock() {
        List<Product> low = productController.getLowStockProducts(10);
        if (!low.isEmpty()) {
            StringBuilder sb = new StringBuilder("The following items have low stock (≤ 10 units):\n\n");
            for (Product p : low) {
                sb.append("  • ").append(p.getName()).append(" — ").append(p.getStock()).append(" left\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // TAB 2: CATEGORIES
    // ═════════════════════════════════════════════════════════════════
    private JPanel buildCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        catTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Description"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        catTable = new JTable(catTableModel);
        catTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Category Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        addFormRow(formPanel, gbc, 0, "Name:", catNameField = new JTextField(15));
        addFormRow(formPanel, gbc, 2, "Description:", catDescField = new JTextField(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton addBtn     = new JButton("Add Category");
        JButton editBtn    = new JButton("Edit Selected");
        JButton deleteBtn  = new JButton("Delete");
        JButton clearBtn   = new JButton("Clear");
        JButton refreshBtn = new JButton("Refresh");
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn); btnPanel.add(refreshBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(catTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> saveCategory(addBtn));
        editBtn.addActionListener(e -> loadCategoryForEdit(addBtn));
        deleteBtn.addActionListener(e -> deleteCategory());
        clearBtn.addActionListener(e -> { catNameField.setText(""); catDescField.setText("");
            editingCatId = -1; addBtn.setText("Add Category"); catTable.clearSelection(); });
        refreshBtn.addActionListener(e -> refreshCategoryTable());

        return panel;
    }

    private void saveCategory(JButton saveBtn) {
        String name = catNameField.getText().trim();
        String desc = catDescField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty.");
            return;
        }

        boolean ok;
        if (editingCatId > 0) {
            ok = categoryDAO.updateCategory(new Category(editingCatId, name, desc));
        } else {
            ok = categoryDAO.addCategory(new Category(name, desc));
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, editingCatId > 0 ? "Category updated!" : "Category added!");
            refreshCategoryTable();
            catNameField.setText(""); catDescField.setText("");
            editingCatId = -1; saveBtn.setText("Add Category");
            loadCategoryCombos();  // refresh product tab combos
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoryForEdit(JButton saveBtn) {
        int row = catTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a category first."); return; }
        editingCatId = (int) catTable.getValueAt(row, 0);
        catNameField.setText((String) catTable.getValueAt(row, 1));
        catDescField.setText((String) catTable.getValueAt(row, 2));
        saveBtn.setText("Save Changes");
    }

    private void deleteCategory() {
        int row = catTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a category to delete."); return; }
        int id = (int) catTable.getValueAt(row, 0);
        String name = (String) catTable.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete category: " + name + "?\n(Products linked to it will have no category.)",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (categoryDAO.deleteCategory(id)) {
                JOptionPane.showMessageDialog(this, "Category deleted.");
                refreshCategoryTable(); loadCategoryCombos();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCategoryTable() {
        catTableModel.setRowCount(0);
        for (Category c : categoryDAO.getAllCategories()) {
            catTableModel.addRow(new Object[]{c.getId(), c.getName(), c.getDescription()});
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // TAB 3: SUPPLIERS
    // ═════════════════════════════════════════════════════════════════
    private JPanel buildSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        supTableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Contact", "Phone", "Email", "Address"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        supTable = new JTable(supTableModel);
        supTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        addFormRow(formPanel, gbc, 0, "Name:", supNameField = new JTextField(15));
        addFormRow(formPanel, gbc, 2, "Contact Name:", supContactField = new JTextField(15));
        gbc.gridy = 1;
        addFormRow(formPanel, gbc, 0, "Phone:", supPhoneField = new JTextField(12));
        addFormRow(formPanel, gbc, 2, "Email:", supEmailField = new JTextField(15));
        gbc.gridy = 2;
        addFormRow(formPanel, gbc, 0, "Address:", supAddressField = new JTextField(25));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton addBtn     = new JButton("Add Supplier");
        JButton editBtn    = new JButton("Edit Selected");
        JButton deleteBtn  = new JButton("Delete");
        JButton clearBtn   = new JButton("Clear");
        JButton refreshBtn = new JButton("Refresh");
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn); btnPanel.add(refreshBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(supTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> saveSupplier(addBtn));
        editBtn.addActionListener(e -> loadSupplierForEdit(addBtn));
        deleteBtn.addActionListener(e -> deleteSupplier());
        clearBtn.addActionListener(e -> clearSupplierForm(addBtn));
        refreshBtn.addActionListener(e -> refreshSupplierTable());

        return panel;
    }

    private void saveSupplier(JButton saveBtn) {
        String name    = supNameField.getText().trim();
        String contact = supContactField.getText().trim();
        String phone   = supPhoneField.getText().trim();
        String email   = supEmailField.getText().trim();
        String address = supAddressField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier name cannot be empty.");
            return;
        }

        boolean ok;
        if (editingSupId > 0) {
            ok = supplierDAO.updateSupplier(new Supplier(editingSupId, name, contact, phone, email, address));
        } else {
            ok = supplierDAO.addSupplier(new Supplier(name, contact, phone, email, address));
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, editingSupId > 0 ? "Supplier updated!" : "Supplier added!");
            refreshSupplierTable(); clearSupplierForm(saveBtn); loadSupplierCombos();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSupplierForEdit(JButton saveBtn) {
        int row = supTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a supplier first."); return; }
        editingSupId = (int) supTable.getValueAt(row, 0);
        supNameField.setText((String) supTable.getValueAt(row, 1));
        supContactField.setText((String) supTable.getValueAt(row, 2));
        supPhoneField.setText((String) supTable.getValueAt(row, 3));
        supEmailField.setText((String) supTable.getValueAt(row, 4));
        supAddressField.setText((String) supTable.getValueAt(row, 5));
        saveBtn.setText("Save Changes");
    }

    private void deleteSupplier() {
        int row = supTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a supplier to delete."); return; }
        int id = (int) supTable.getValueAt(row, 0);
        String name = (String) supTable.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete supplier: " + name + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (supplierDAO.deleteSupplier(id)) {
                JOptionPane.showMessageDialog(this, "Supplier deleted.");
                refreshSupplierTable(); loadSupplierCombos();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshSupplierTable() {
        supTableModel.setRowCount(0);
        for (Supplier s : supplierDAO.getAllSuppliers()) {
            supTableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getContactName(), s.getPhone(), s.getEmail(), s.getAddress()
            });
        }
    }

    private void clearSupplierForm(JButton saveBtn) {
        supNameField.setText(""); supContactField.setText(""); supPhoneField.setText("");
        supEmailField.setText(""); supAddressField.setText("");
        editingSupId = -1; saveBtn.setText("Add Supplier"); supTable.clearSelection();
    }

    // ═════════════════════════════════════════════════════════════════
    // TAB 4: STOCK IN
    // ═════════════════════════════════════════════════════════════════
    private JPanel buildStockInPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        siTableModel = new DefaultTableModel(
            new String[]{"ID", "Product", "Quantity", "Remarks", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        siTable = new JTable(siTableModel);
        siTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Record Stock In"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        siProductCombo = new JComboBox<>();
        addFormRow(formPanel, gbc, 0, "Product:", siProductCombo);
        addFormRow(formPanel, gbc, 2, "Quantity:", siQtyField = new JTextField(8));
        gbc.gridy = 1;
        siRemarksField = new JTextField(20);
        addFormRow(formPanel, gbc, 0, "Remarks:", siRemarksField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton recordBtn  = new JButton("Record Stock In");
        JButton refreshBtn = new JButton("Refresh History");
        btnPanel.add(recordBtn); btnPanel.add(refreshBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(siTable), BorderLayout.CENTER);

        recordBtn.addActionListener(e -> recordStockIn());
        refreshBtn.addActionListener(e -> refreshStockInTable());

        return panel;
    }

    private void recordStockIn() {
        if (siProductCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No products available. Add a product first.");
            return;
        }
        Product selected = (Product) siProductCombo.getSelectedItem();
        String qty     = siQtyField.getText().trim();
        String remarks = siRemarksField.getText().trim();

        String result = stockController.recordStockIn(selected.getId(), qty, remarks);

        if (BaseController.isSuccess(result)) {
            JOptionPane.showMessageDialog(this,
                "Stock added: " + qty + " × " + selected.getName());
            siQtyField.setText(""); siRemarksField.setText("");
            refreshStockInTable();
            refreshProductTable();  // update stock count in Products tab
        } else {
            JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshStockInTable() {
        siTableModel.setRowCount(0);
        for (StockIn si : stockController.getAllStockIn()) {
            siTableModel.addRow(new Object[]{
                si.getId(), si.getProductName(), si.getQuantity(),
                si.getRemarks(), si.getCreatedAt()
            });
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // TAB 5: STOCK OUT
    // ═════════════════════════════════════════════════════════════════
    private JPanel buildStockOutPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        soTableModel = new DefaultTableModel(
            new String[]{"ID", "Product", "Quantity", "Reason", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        soTable = new JTable(soTableModel);
        soTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Record Stock Out"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        soProductCombo = new JComboBox<>();
        addFormRow(formPanel, gbc, 0, "Product:", soProductCombo);
        addFormRow(formPanel, gbc, 2, "Quantity:", soQtyField = new JTextField(8));
        gbc.gridy = 1;
        soReasonsCombo = new JComboBox<>(new String[]{
            "Used for Orders", "Wasted / Spoiled", "Inventory Adjustment"
        });
        addFormRow(formPanel, gbc, 0, "Reason:", soReasonsCombo);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton recordBtn  = new JButton("Record Stock Out");
        JButton refreshBtn = new JButton("Refresh History");
        btnPanel.add(recordBtn); btnPanel.add(refreshBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(soTable), BorderLayout.CENTER);

        recordBtn.addActionListener(e -> recordStockOut());
        refreshBtn.addActionListener(e -> refreshStockOutTable());

        return panel;
    }

    private void recordStockOut() {
        if (soProductCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No products available.");
            return;
        }
        Product selected = (Product) soProductCombo.getSelectedItem();
        String qty    = soQtyField.getText().trim();
        String reason = (String) soReasonsCombo.getSelectedItem();

        String result = stockController.recordStockOut(selected.getId(), qty, reason);

        if (BaseController.isSuccess(result)) {
            JOptionPane.showMessageDialog(this,
                "Stock deducted: " + qty + " × " + selected.getName());
            soQtyField.setText("");
            refreshStockOutTable();
            refreshProductTable();  // update stock count in Products tab
        } else {
            JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshStockOutTable() {
        soTableModel.setRowCount(0);
        for (StockOut so : stockController.getAllStockOut()) {
            soTableModel.addRow(new Object[]{
                so.getId(), so.getProductName(), so.getQuantity(),
                so.getRemarks(), so.getCreatedAt()
            });
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // HELPERS — Combo loaders and form utilities
    // ═════════════════════════════════════════════════════════════════

    /** Loads Category objects from DB into the Products tab category combo. */
    private void loadCategoryCombos() {
        pCategoryCombo.removeAllItems();
        for (Category c : categoryDAO.getAllCategories()) {
            pCategoryCombo.addItem(c);   // Category.toString() returns name automatically
        }
    }

    /** Loads Supplier objects from DB into the Products tab supplier combo. */
    private void loadSupplierCombos() {
        pSupplierCombo.removeAllItems();
        for (Supplier s : supplierDAO.getAllSuppliers()) {
            pSupplierCombo.addItem(s);   // Supplier.toString() returns name automatically
        }
    }

    /** Loads Product objects into the Stock In and Stock Out combos. */
    private void loadProductCombos() {
        siProductCombo.removeAllItems();
        soProductCombo.removeAllItems();
        for (Product p : productController.getAllProducts()) {
            siProductCombo.addItem(p);   // Product.toString() shows name + stock
            soProductCombo.addItem(p);
        }
    }

    /** Selects a combo item by its toString() display name. */
    private <T> void selectComboByName(JComboBox<T> combo, String name) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).toString().equals(name)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Reusable helper: adds a label + component pair to a GridBagLayout panel.
     * Avoids repeating 6 lines of GridBagConstraints setup for every form field.
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            int startCol, String labelText, JComponent field) {
        gbc.gridx = startCol;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = startCol + 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    // ═════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ═════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show login dialog first
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (!loginDialog.isLoginSuccess()) {
                // User closed the dialog without logging in — exit the app
                System.exit(0);
            }

            // Get the logged-in user's display name for the window title
            String displayName = SessionManager.getInstance().getCurrentUser().getFullName()
                + " [" + SessionManager.getInstance().getCurrentUser().getRole() + "]";

            new SimpleUI(displayName);
        });
    }
}
