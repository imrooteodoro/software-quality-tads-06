package util;

public class Money {
    public static String formatCents(int cents) {
        return String.format("R$ %.2f", cents / 100.0);
    }

    public static String formatDouble(double value) {
        return String.format("R$ %.2f", value);
    }
}
