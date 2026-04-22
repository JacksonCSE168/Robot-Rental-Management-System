import java.util.Scanner;
import java.sql.*;

public class RentalMenu {

    // 1. Renting checkouts 
    public static void reportRentingCheckouts(Connection conn, Scanner scanner) {
        System.out.print("Enter Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());

        String sql = "SELECT c.customer_id, c.first_name, c.last_name, COUNT(rr.robot_id) AS total_items_rented " +
                     "FROM Customer c " +
                     "JOIN Rental r ON c.customer_id = r.customer_id " +
                     "JOIN Rental_Robot rr ON r.rental_id = rr.rental_id " +
                     "WHERE c.customer_id = ? " +
                     "GROUP BY c.customer_id, c.first_name, c.last_name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 使用 PreparedStatement 防止 SQL 注入
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Report 1: Renting Checkouts ---");
                System.out.println("Customer: " + rs.getString("first_name") + " " + rs.getString("last_name"));
                System.out.println("Total rented items: " + rs.getInt("total_items_rented"));
            } else {
                System.out.println("No rental records found for Customer ID " + customerId);
            }
        } catch (SQLException e) {
            System.out.println("Query Error: " + e.getMessage());
        }
    }

    
    // 2. Popular robot 
    
    public static void reportPopularRobot(Connection conn) {
    String sql = "SELECT rb.robot_id, rb.name, " +
                 "COUNT(rr.rental_id) AS number_of_times_rented " +
                 "FROM Robot rb " +
                 "LEFT JOIN Rental_Robot rr ON rb.robot_id = rr.robot_id " +
                 "GROUP BY rb.robot_id, rb.name " +
                 "ORDER BY number_of_times_rented DESC LIMIT 1";

    try {
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("\n--- Report 2: Popular Robot ---");
            System.out.println("Robot: " + rs.getString("name"));
            System.out.println("Times rented: " + rs.getInt("number_of_times_rented"));
        } else {
            System.out.println("No data available.");
        }

    } catch (SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
    }
}

   
    // 3. Popular Manufacturer (最受欢迎的制造商)
    
    public static void reportPopularManufacturer(Connection conn) {
    String sql = "SELECT rm.manufacturer, COUNT(rr.robot_id) AS total_rented_units " +
                 "FROM Robot rb " +
                 "JOIN ROBOT_MODEL rm ON rb.model = rm.model " +
                 "LEFT JOIN Rental_Robot rr ON rb.robot_id = rr.robot_id " +
                 "GROUP BY rm.manufacturer " +
                 "ORDER BY total_rented_units DESC LIMIT 1";

    try (PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            System.out.println("\n--- Report 3: Popular Manufacturer ---");
            System.out.println("Most popular manufacturer: " + rs.getString("manufacturer"));
            System.out.println("Total rented units: " + rs.getInt("total_rented_units"));
        } else {
            System.out.println("No data available for popular manufacturer report.");
        }

    } catch (SQLException e) {
        System.out.println("Query Error: " + e.getMessage());
    }
}

    
   public static void reportPopularCar(Connection conn) {
    String sql = "SELECT dc.car_id, dc.model, dc.serial_number, " +
                 "COUNT(rr.robot_id) AS total_robots_delivered, " +
                 "ROUND(SUM(COALESCE(c.facility_distance, 0)), 2) AS estimated_total_miles " +
                 "FROM Driverless_car dc " +
                 "LEFT JOIN Rental r ON dc.car_id = r.car_id " +
                 "LEFT JOIN Customer c ON r.customer_id = c.customer_id " +
                 "LEFT JOIN Rental_Robot rr ON r.rental_id = rr.rental_id " +
                 "GROUP BY dc.car_id, dc.model, dc.serial_number " +
                 "ORDER BY total_robots_delivered DESC, estimated_total_miles DESC LIMIT 1";

    try (PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            System.out.println("\n--- Report 4: Popular Driverless Car ---");
            System.out.println("Most used driverless car: " + rs.getString("model"));
            System.out.println("Serial number: " + rs.getString("serial_number"));

            int count = rs.getInt("total_robots_delivered");
            double miles = rs.getDouble("estimated_total_miles");

            if (count == 0) {
                System.out.println("No deliveries yet.");
            } else {
                System.out.println("Robots delivered: " + count);
                System.out.println("Estimated total miles: " + miles);
            }
        } else {
            System.out.println("No data available for driverless car report.");
        }

    } catch (SQLException e) {
        System.out.println("Query Error: " + e.getMessage());
    }
}

   
    // 5. Robots checked out 
    
   public static void reportRobotsCheckedOut(Connection conn) {
    String sql = "SELECT c.customer_id, c.first_name, c.last_name, " +
                 "COUNT(rr.robot_id) AS total_robots_rented " +
                 "FROM Customer c " +
                 "LEFT JOIN Rental r ON c.customer_id = r.customer_id " +
                 "LEFT JOIN Rental_Robot rr ON r.rental_id = rr.rental_id " +
                 "GROUP BY c.customer_id, c.first_name, c.last_name " +
                 "ORDER BY total_robots_rented DESC LIMIT 1";

    try (PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            System.out.println("\n--- Report 5: Robots Checked Out ---");

            String name = rs.getString("first_name") + " " + rs.getString("last_name");
            int count = rs.getInt("total_robots_rented");

            System.out.println("Top customer: " + name);

            if (count == 0) {
                System.out.println("No robots rented yet.");
            } else {
                System.out.println("Total robots rented: " + count);
            }
        } else {
            System.out.println("No data available for robots checked out report.");
        }

    } catch (SQLException e) {
        System.out.println("Query Error: " + e.getMessage());
    }
}

    
    // 6. Robots by Type 
    public static void reportRobotsByType(Connection conn, Scanner scanner) {
    System.out.print("Enter Robot Type (e.g., Cleaning, Security, Assistance): ");
    String robotType = scanner.nextLine();

    System.out.print("Enter release Year limit (e.g., 2025): ");
    int yearLimit = Integer.parseInt(scanner.nextLine());

    String sql = "SELECT rb.robot_id, rb.name, rm.manufacturer, rb.year " +
                 "FROM Robot rb " +
                 "JOIN ROBOT_MODEL rm ON rb.model = rm.model " +
                 "WHERE rm.primary_function = ? AND rb.year < ? " +
                 "ORDER BY rb.year, rb.name";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, robotType);
        pstmt.setInt(2, yearLimit);

        ResultSet rs = pstmt.executeQuery();

        System.out.println("\n--- Report 6: Robots by Type ---");
        System.out.println("Robots of type: " + robotType + " released before " + yearLimit);

        boolean found = false;

        while (rs.next()) {
            found = true;
            System.out.println(rs.getString("name") + " | " +
                               rs.getString("manufacturer") + " | " +
                               rs.getInt("year"));
        }

        if (!found) {
            System.out.println("No robots found matching the criteria.");
        }

    } catch (SQLException e) {
        System.out.println("Query Error: " + e.getMessage());
    }
}


}
