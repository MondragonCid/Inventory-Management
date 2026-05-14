package main;

import controller.AuthController;
import controller.BaseController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * FILE: main/LoginDialog.java
 * ROLE: Login screen shown before the main inventory window opens.
 *
 * Uses AuthController.login() — never touches UserDAO directly.
 * On success, disposes itself and lets SimpleUI open.
 * On failure, shows the error message returned by the controller.
 *
 * Default credentials (from database.sql seed data):
 *   Admin:  admin  / admin123
 *   Staff:  staff01 / staff123
 */
public class LoginDialog extends JDialog {

    private final AuthController authController = new AuthController();
    private boolean loginSuccess = false;

    public LoginDialog(Frame parent) {
        super(parent, "Coffee Shop Inventory — Login", true);
        setSize(380, 240);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // ── Layout ────────────────────────────────────────────────────
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Coffee Shop Inventory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(passwordField, gbc);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        add(panel);

        // ── Actions ───────────────────────────────────────────────────
        ActionEvent[] dummy = {null};  // shared trigger for Enter key

        // Press Enter in either field to trigger login
        usernameField.addActionListener(e -> loginBtn.doClick());
        passwordField.addActionListener(e -> loginBtn.doClick());

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String result = authController.login(username, password);

            if (BaseController.isSuccess(result)) {
                loginSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    result,
                    "Login Failed",
                    JOptionPane.WARNING_MESSAGE
                );
                passwordField.setText("");
                passwordField.requestFocus();
            }
        });
    }

    /**
     * Returns true if the user successfully logged in.
     * SimpleUI checks this after the dialog closes.
     */
    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}
