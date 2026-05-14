package model;

/**
 * FILE: model/Category.java
 * ROLE: Represents one row in the `categories` table.
 *
 * OOP — Encapsulation: all fields private, accessed via getters/setters.
 */
public class Category {

    private int    id;
    private String name;
    private String description;

    // ── Constructor: for NEW categories ──────────────────────────────
    public Category(String name, String description) {
        this.name        = name;
        this.description = description;
    }

    // ── Constructor: for categories LOADED from the database ─────────
    public Category(int id, String name, String description) {
        this.id          = id;
        this.name        = name;
        this.description = description;
    }

    // ── Getters ───────────────────────────────────────────────────────
    public int    getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }

    // ── Setters ───────────────────────────────────────────────────────
    public void setId(int id)                { this.id          = id; }
    public void setName(String name)         { this.name        = name; }
    public void setDescription(String desc)  { this.description = desc; }

    // toString used by JComboBox to display the name automatically
    @Override
    public String toString() { return name; }
}
