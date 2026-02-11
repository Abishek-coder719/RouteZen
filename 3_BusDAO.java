package com.routezen.dao;

import com.routezen.model.Bus;
import com.routezen.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bus operations
 * Handles all database interactions for Bus entity
 * 
 * @author Route Zen Team
 * @version 1.0
 */
public class BusDAO {

    /**
     * Search buses between two stops at a given time
     * 
     * @param fromStopId Origin stop ID
     * @param toStopId Destination stop ID
     * @param departureTime Minimum departure time
     * @return List of available buses
     */
    public List<Bus> searchBuses(int fromStopId, int toStopId, String departureTime) {
        List<Bus> buses = new ArrayList<>();

        String query = "SELECT DISTINCT b.*, s.departure_time, s.arrival_time, " +
                      "s.fare, s.available_seats " +
                      "FROM buses b " +
                      "INNER JOIN schedules s ON b.bus_id = s.bus_id " +
                      "WHERE s.from_stop_id = ? " +
                      "AND s.to_stop_id = ? " +
                      "AND s.departure_time >= ? " +
                      "AND s.available_seats > 0 " +
                      "ORDER BY s.departure_time ASC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(query);

            // Set parameters
            pstmt.setInt(1, fromStopId);
            pstmt.setInt(2, toStopId);
            pstmt.setString(3, departureTime);

            rs = pstmt.executeQuery();

            // Process results
            while (rs.next()) {
                Bus bus = new Bus();
                bus.setBusId(rs.getInt("bus_id"));
                bus.setBusNumber(rs.getString("bus_number"));
                bus.setBusType(rs.getString("bus_type"));
                bus.setOperator(rs.getString("operator"));
                bus.setTotalSeats(rs.getInt("total_seats"));
                bus.setWifiAvailable(rs.getBoolean("wifi_available"));
                bus.setAcAvailable(rs.getBoolean("ac_available"));
                bus.setDepartureTime(rs.getTime("departure_time"));
                bus.setArrivalTime(rs.getTime("arrival_time"));
                bus.setFare(rs.getDouble("fare"));
                bus.setAvailableSeats(rs.getInt("available_seats"));

                buses.add(bus);
            }

            System.out.println("✓ Found " + buses.size() + " buses");

        } catch (SQLException e) {
            System.err.println("✗ Error searching buses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return buses;
    }

    /**
     * Get all buses
     * 
     * @return List of all buses in the system
     */
    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        String query = "SELECT * FROM buses ORDER BY bus_type, bus_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Bus bus = new Bus();
                bus.setBusId(rs.getInt("bus_id"));
                bus.setBusNumber(rs.getString("bus_number"));
                bus.setBusType(rs.getString("bus_type"));
                bus.setOperator(rs.getString("operator"));
                bus.setTotalSeats(rs.getInt("total_seats"));
                buses.add(bus);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all buses: " + e.getMessage());
        }

        return buses;
    }

    /**
     * Get bus by ID
     * 
     * @param busId Bus ID to search
     * @return Bus object or null if not found
     */
    public Bus getBusById(int busId) {
        String query = "SELECT * FROM buses WHERE bus_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, busId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Bus bus = new Bus();
                bus.setBusId(rs.getInt("bus_id"));
                bus.setBusNumber(rs.getString("bus_number"));
                bus.setBusType(rs.getString("bus_type"));
                bus.setOperator(rs.getString("operator"));
                return bus;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching bus: " + e.getMessage());
        }

        return null;
    }
}
