/**
 * RewardsManager - Business logic layer for the Rewards Tracker system.
 * Handles all points-related operations and database interactions.
 * Validates input and manages points calculations.
 */
package reward_app;

import java.sql.*;

public class RewardsManager {
    private RewardsDatabase db;

    /**
     * Initializes the rewards manager and establishes database connection.
     */
    public RewardsManager() {
        db = new RewardsDatabase();
    }

    /**
     * Adds points to a customer's account based on purchase amount.
     * Creates new customer record if phone number doesn't exist.
     * 
     * @param phone Customer's phone number (must be 10 digits)
     * @param amount Purchase amount (converted to whole points)
     * @throws SQLException If database operation fails
     * @throws IllegalArgumentException If phone number is invalid
     */
    public void addPoints(String phone, double amount) throws SQLException {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }

        int points = (int) Math.round(amount);
        String sql = "INSERT INTO rewards (phone, points) VALUES (?, ?) "
                  + "ON CONFLICT(phone) DO UPDATE SET points = points + ?";
        
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, phone);
            pstmt.setInt(2, points);
            pstmt.setInt(3, points);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves current point balance for a customer.
     * Returns 0 for new customers.
     * 
     * @param phone Customer's phone number (must be 10 digits)
     * @return Current point balance
     * @throws SQLException If database operation fails
     * @throws IllegalArgumentException If phone number is invalid
     */
    public int checkPoints(String phone) throws SQLException {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }

        String sql = "SELECT points FROM rewards WHERE phone = ?";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("points") : 0;
        }
    }

    /**
     * Deducts points from customer's account for redemption.
     * Validates sufficient points are available before deducting.
     * 
     * @param phone Customer's phone number (must be 10 digits)
     * @param points Number of points to redeem (must be positive)
     * @throws SQLException If database operation fails
     * @throws IllegalArgumentException If points invalid or insufficient
     */
    public void subtractPoints(String phone, int points) throws SQLException {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Points must be positive");
        }

        int currentPoints = checkPoints(phone);
        if (currentPoints < points) {
            throw new IllegalArgumentException("Not enough points available");
        }

        String sql = "UPDATE rewards SET points = points - ? WHERE phone = ?";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
        }
    }

    /**
     * Validates phone number format.
     * Checks for exactly 10 digits with no other characters.
     * 
     * @param phone Phone number to validate
     * @return true if phone number is valid, false otherwise
     */
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }
}