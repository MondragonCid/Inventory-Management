package util;

/**
 * FILE: util/InputValidator.java
 * ROLE: Reusable input validation helper
 *
 * All DAO and controller classes use these static methods to
 * validate user input before sending data to the database.
 * Centralizing validation here avoids duplicating checks everywhere.
 */
public class InputValidator {

    private InputValidator() {}  // Utility class — no instantiation

    /**
     * Returns true if the string is null or contains only whitespace.
     * Use this to check text field inputs before saving.
     *
     * Example:
     *   if (InputValidator.isEmpty(nameField.getText())) {
     *       JOptionPane.showMessageDialog(this, "Name cannot be empty.");
     *       return;
     *   }
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Returns true if the string is a valid positive number (price/quantity).
     * Accepts decimals (e.g. "185.00") and integers (e.g. "50").
     */
    public static boolean isPositiveNumber(String value) {
        if (isEmpty(value)) return false;
        try {
            double d = Double.parseDouble(value.trim());
            return d > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true if the string is a valid non-negative integer (stock count).
     * Accepts "0" and above. Rejects decimals and negatives.
     */
    public static boolean isNonNegativeInt(String value) {
        if (isEmpty(value)) return false;
        try {
            int i = Integer.parseInt(value.trim());
            return i >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true if the string is a valid positive integer (e.g. stock_in qty).
     */
    public static boolean isPositiveInt(String value) {
        if (isEmpty(value)) return false;
        try {
            int i = Integer.parseInt(value.trim());
            return i > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true if the string looks like a valid email address.
     * Simple check — enough for a student project.
     */
    public static boolean isValidEmail(String value) {
        if (isEmpty(value)) return false;
        return value.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Returns true if the string looks like a valid PH phone number.
     * Accepts formats: 09XXXXXXXXX (11 digits) or +639XXXXXXXXX
     */
    public static boolean isValidPhone(String value) {
        if (isEmpty(value)) return false;
        String cleaned = value.trim().replaceAll("[\\s\\-]", "");
        return cleaned.matches("^(09\\d{9}|\\+639\\d{9})$");
    }
}

