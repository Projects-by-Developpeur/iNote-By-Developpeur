package com.example.inote;

public class StronPassword {

    public static boolean isStrongPassword(String password) {
        // Regex pattern to enforce password strength rules
        String regexPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        // Check if the password matches the regex pattern
        return password.matches(regexPattern);
    }
}
