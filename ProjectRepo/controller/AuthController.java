package controller;

import authenticator.SessionManager;
import dao.UserDAO;
import model.User;
import util.InputValidator;

/**
 * FILE: controller/AuthController.java
 * ROLE: Handles login and logout logic.
 *
 * OOP — INHERITANCE: extends BaseController
 * OOP — ABSTRACTION: UI calls login() and gets "OK" or an error string;
 *                    it never knows about SQL or SessionManager details.
 */
public class AuthController extends BaseController {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Validates inputs, queries the DB, starts the session.
     * @return "OK" on success, or a user-friendly error message.
     */
    public String login(String username, String password) {
        if (InputValidator.isEmpty(username)) return "Please enter your username.";
        if (InputValidator.isEmpty(password)) return "Please enter your password.";

        User user = userDAO.login(username, password);

        if (user == null)        return "Invalid username or password. Please try again.";
        if (!user.isActive())    return "Your account has been deactivated. Contact the Admin.";

        SessionManager.getInstance().setCurrentUser(user);
        System.out.println("[Auth] Login: " + user.getUsername() + " (" + user.getRole() + ")");
        return success();
    }

    /** Clears the current session. */
    public void logout() {
        SessionManager.getInstance().logout();
    }

    /** Returns the currently logged-in user, or null. */
    public User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }

    /** Returns true if the current user has the ADMIN role. */
    public boolean isAdmin() {
        return SessionManager.getInstance().isAdmin();
    }
}
