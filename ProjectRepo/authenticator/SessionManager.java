package authenticator;

import model.User;

/**
 * FILE: authenticator/SessionManager.java
 * ROLE: Tracks the currently logged-in user for the whole session.
 *
 * OOP — Singleton Pattern: only one session can exist at a time.
 *
 * BUG FIXED from original:
 *   - Class was completely empty
 *   - Had no fields, no methods, and no access modifier on the class
 *   - Fixed: added public modifier, currentUser field, and all session methods
 *
 * Usage:
 *   // After successful login:
 *   SessionManager.getInstance().setCurrentUser(user);
 *
 *   // In any form to check who is logged in:
 *   User u = SessionManager.getInstance().getCurrentUser();
 *   if (u.isAdmin()) { showAdminControls(); }
 *
 *   // On logout:
 *   SessionManager.getInstance().logout();
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}     // Prevent direct instantiation

    /** Returns the single shared SessionManager instance. */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /** Stores the user who just logged in. */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /** Returns the currently logged-in User, or null if not logged in. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Returns true if someone is currently logged in. */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /** Returns true if the current user is an Admin. */
    public boolean isAdmin() {
        return isLoggedIn() && currentUser.isAdmin();
    }

    /** Returns true if the current user is Staff. */
    public boolean isStaff() {
        return isLoggedIn() && currentUser.isStaff();
    }

    /** Clears the session (call on logout or window close). */
    public void logout() {
        System.out.println("[Session] User '" + (currentUser != null ? currentUser.getUsername() : "?") + "' logged out.");
        currentUser = null;
    }
}
