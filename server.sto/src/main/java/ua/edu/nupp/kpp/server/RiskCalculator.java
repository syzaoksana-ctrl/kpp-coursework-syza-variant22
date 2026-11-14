package ua.edu.nupp.kpp.server;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
public final class RiskCalculator {

    public static final double W_F1 = 0.40;
    public static final double W_F2 = 0.35;
    public static final double W_F3 = 0.25;

    public static final int MAX_WAIT_DAYS = 30;

    private RiskCalculator() {
    }

    public static double normalizeF1(int safetyLevel) {
        checkLevel(safetyLevel);
        return safetyLevel * 25.0;
    }

    public static double normalizeF2(int mobilityLevel) {
        checkLevel(mobilityLevel);
        return mobilityLevel * 25.0;
    }

    public static double calculateF3(LocalDate createdAt, LocalDate today) {

        if (createdAt == null || today == null) {
            throw new IllegalArgumentException("Dates are required");
        }

        long days = ChronoUnit.DAYS.between(createdAt, today);

        if (days < 0) {
            throw new IllegalArgumentException(
                    "Creation date cannot be in the future"
            );
        }

        return Math.min(days, MAX_WAIT_DAYS) * 100.0
                / MAX_WAIT_DAYS;
    }

    private static void checkLevel(int level) {
        if (level < 0 || level > 4) {
            throw new IllegalArgumentException(
                    "Level must be between 0 and 4"
            );
        }
    }

    public static void checkFactor(String name, double value) {

        if (!Double.isFinite(value) || value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException(
                    "Factor " + name + " must be between 0 and 100"
            );
        }
    }

    // Integral indicator calculation
    public static double calculateIntegral(
            double f1, double f2, double f3) {

        checkFactor("F1", f1);
        checkFactor("F2", f2);
        checkFactor("F3", f3);

        double result = W_F1 * f1
                + W_F2 * f2
                + W_F3 * f3;

        return Math.round(result * 100.0) / 100.0;
    }
}