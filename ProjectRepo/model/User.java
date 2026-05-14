package model;

/**
 * FILE: model/User.java
 * ROLE: Represents one row in the `users` table.
 *
 * OOP — Encapsulation: all fields private, accessed via getters/setters.
 *
 * BUG FIXED from original:
 *   - Was in package ProjectRepo.authenticator (wrong — caused import errors)
 *   - Class was completely empty (no fields, no constructors, no methods)
 *   - Now moved to model package to follow MVC structure
 */
public class User {

    // Role constants — avoids "magic strings" like "admin" scattered in code
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STAFF = "STAFF";

    private int     id;
    private String  username;
    private String  password;
    private String  role;       // "ADMIN" or "STAFF"
    private String  fullName;
    private boolean isActive;

    // ── Constructor: for NEW users ────────────────────────────────────
    public User(String username, String password, String role, String fullName) {
        this.username  = username;
        this.password  = password;
        this.role      = role;
        this.fullName  = fullName;
        this.isActive  = true;
    }

    // ── Constructor: for users LOADED from the database ───────────────
    public User(int id, String username, String password,
                String role, String fullName, boolean isActive) {
        this.id        = id;
        this.username  = username;
        this.password  = password;
        this.role      = role;
        this.fullName  = fullName;
        this.isActive  = isActive;
    }

    // ── Getters ───────────────────────────────────────────────────────
    public int     getId()        { return id; }
    public String  getUsername()  { return username; }
    public String  getPassword()  { return password; }
    public String  getRole()      { return role; }
    public String  getFullName()  { return fullName; }
    public boolean isActive()     { return isActive; }

    // ── Setters ───────────────────────────────────────────────────────
    public void setId(int id)              { this.id       = id; }
    public void setUsername(String u)      { this.username = u; }
    public void setPassword(String p)      { this.password = p; }
    public void setRole(String role)       { this.role     = role; }
    public void setFullName(String name)   { this.fullName = name; }
    public void setActive(boolean active)  { this.isActive = active; }

    // ── Convenience role checks ───────────────────────────────────────
    public boolean isAdmin() { return ROLE_ADMIN.equals(role); }
    public boolean isStaff() { return ROLE_STAFF.equals(role); }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', role='%s', name='%s'}",
                id, username, role, fullName);
    }
}
