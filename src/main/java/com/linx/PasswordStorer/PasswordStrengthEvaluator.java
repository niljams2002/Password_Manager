package com.linx.PasswordStorer;

public class PasswordStrengthEvaluator {
    public PasswordStrengthEvaluator(){

    }

    /**
     * Method to evaluate the strength of a password
     * @param password the password the strength of which must be measured
     * @return integer specifying password strength
     */
    public int evaluatePasswordStrength(String password) {
        // 0 - empty string
        // 1 - Very Weak
        // 2 - Weak
        // 3 - Fair
        // 4 - Good
        // 5 - Great

        int strength = 0;

        if (password.length() >= 8) {
            strength += 1; // at least 8 characters
        }
        if (password.matches(".*[a-z].*")) {
            strength++; // contains lowercase letters
        }
        if (password.matches(".*[A-Z].*")) {
            strength++; // contains uppercase letters
        }
        if (password.matches(".*[0-9].*")) {
            strength++; // contains numbers
        }
        if (password.matches(".*[!@#$%^&*+=?_-].*")) {
            strength++; // contains special characters
        }

        if (password.length() == 0){
            strength = 0;
        }

        return strength;
    }
}
