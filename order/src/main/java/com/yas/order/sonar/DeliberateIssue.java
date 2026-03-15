package com.yas.order.sonar;

public class DeliberateIssue {

    // Hardcoded credential - Sonar should flag this (e.g. S2068)
    private static final String DB_PASSWORD = "ChangeMe123!";

    // Use of System.out - Sonar rule flags direct use of stdout (S106)
    public void printDebug() {
        System.out.println("Debug: connecting with password=" + DB_PASSWORD);
    }

    // Empty catch block - bad practice (Sonar S1166)
    public void riskyOperation() {
        try {
            int x = 1 / 1;
        } catch (Exception e) {
            // intentionally swallowed
        }
    }
}
