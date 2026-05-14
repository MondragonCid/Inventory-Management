package model;

/**
 * FILE: model/Supplier.java
 * ROLE: Represents one row in the `suppliers` table.
 *
 * OOP — Encapsulation: all fields private, accessed via getters/setters.
 */
public class Supplier {

    private int    id;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String address;

    // ── Constructor: for NEW suppliers ───────────────────────────────
    public Supplier(String name, String contactName,
                    String phone, String email, String address) {
        this.name        = name;
        this.contactName = contactName;
        this.phone       = phone;
        this.email       = email;
        this.address     = address;
    }

    // ── Constructor: for suppliers LOADED from the database ──────────
    public Supplier(int id, String name, String contactName,
                    String phone, String email, String address) {
        this.id          = id;
        this.name        = name;
        this.contactName = contactName;
        this.phone       = phone;
        this.email       = email;
        this.address     = address;
    }

    // ── Getters ───────────────────────────────────────────────────────
    public int    getId()          { return id; }
    public String getName()        { return name; }
    public String getContactName() { return contactName; }
    public String getPhone()       { return phone; }
    public String getEmail()       { return email; }
    public String getAddress()     { return address; }

    // ── Setters ───────────────────────────────────────────────────────
    public void setId(int id)                { this.id          = id; }
    public void setName(String name)         { this.name        = name; }
    public void setContactName(String cn)    { this.contactName = cn; }
    public void setPhone(String phone)       { this.phone       = phone; }
    public void setEmail(String email)       { this.email       = email; }
    public void setAddress(String address)   { this.address     = address; }

    // toString used by JComboBox to display the name automatically
    @Override
    public String toString() { return name; }
}
