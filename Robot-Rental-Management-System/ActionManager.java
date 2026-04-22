import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Scanner;

public class ActionManager {
    public static String nextSanitizedLine(Scanner s) {
        String line = s.nextLine();
        return line.replaceAll("[^a-zA-Z0-9@.:\\-]", "");
    }

   public static void rentRobotPrompt(Connection conn, Scanner s) {
     try{
        System.out.println("Enter rental ID:");
        int rentalId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter customer ID:");
        int customerId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter robot ID:");
        int robotId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter car ID:");
        int carId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter checkout date (YYYY-MM-DD):");
        String checkoutDate = nextSanitizedLine(s);

        System.out.println("Enter due date (YYYY-MM-DD):");
        String dueDate = nextSanitizedLine(s);

       
        String rentalSQL = "INSERT INTO Rental (rental_id, customer_id, car_id, checkout_date, due_date) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement rentalStmt = conn.prepareStatement(rentalSQL);
        rentalStmt.setInt(1, rentalId);
        rentalStmt.setInt(2, customerId);
        rentalStmt.setInt(3, carId);
        rentalStmt.setString(4, checkoutDate);
        rentalStmt.setString(5, dueDate);
        rentalStmt.executeUpdate();

        
        String rrSQL = "INSERT INTO Rental_Robot (rental_id, robot_id) VALUES (?, ?)";
        PreparedStatement rrStmt = conn.prepareStatement(rrSQL);
        rrStmt.setInt(1, rentalId);
        rrStmt.setInt(2, robotId);
        rrStmt.executeUpdate();

        System.out.println("Robot rental recorded successfully.");

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

    public static void returnEquipmentPrompt(Connection conn, Scanner s) {
    try {
        System.out.println("Enter rental ID:");
        int rentalId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter return date (YYYY-MM-DD):");
        String returnDate = nextSanitizedLine(s);

        String sql = "UPDATE Rental SET return_date = ? WHERE rental_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, returnDate);
        stmt.setInt(2, rentalId);
        stmt.executeUpdate();

        System.out.println("Return recorded successfully.");

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

   public static void deliveryPrompt(Connection conn, Scanner s) {
    try {
        System.out.println("Enter rental ID:");
        int rentalId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter car ID:");
        int carId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter dispatch time:");
        String dispatchTime = nextSanitizedLine(s);

        System.out.println("Enter arrival time:");
        String arrivalTime = nextSanitizedLine(s);

        String sql = "INSERT INTO Delivery_Trip (rental_id, trip_type, car_id, dispatch_time, arrival_time) VALUES (?, 'delivery', ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, rentalId);
        stmt.setInt(2, carId);
        stmt.setString(3, dispatchTime);
        stmt.setString(4, arrivalTime);
        stmt.executeUpdate();

        System.out.println("Delivery recorded.");

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

    public static void pickupPrompt(Connection conn, Scanner s) {
    try {
        System.out.println("Enter rental ID:");
        int rentalId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter car ID:");
        int carId = Integer.parseInt(nextSanitizedLine(s));

        System.out.println("Enter pickup time:");
        String pickupTime = nextSanitizedLine(s);

        String sql = "INSERT INTO Delivery_Trip (rental_id, trip_type, car_id, completion_time) VALUES (?, 'pickup', ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, rentalId);
        stmt.setInt(2, carId);
        stmt.setString(3, pickupTime);
        stmt.executeUpdate();

        System.out.println("Pickup recorded.");

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
}