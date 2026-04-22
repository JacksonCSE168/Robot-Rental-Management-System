import java.util.Scanner;
import java.sql.*;

public class Main {
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DB_URL = "jdbc:sqlite:CSE3241project.db";

    public static Connection initializeDB() {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("The connection to the database was successful.");
            } else {
                System.out.println("NULL CONNECTION");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException se) {
            System.out.println("Could not connect to database.");
            se.printStackTrace();
        }
        return conn;
    }

    public static String nextSanitizedLine(Scanner s) {
        String line = s.nextLine();
        return line.replaceAll("[^a-zA-Z0-9@.:\\-]", "");
    }

    public static void main(String[] args) {
        Connection conn = initializeDB();
       
        
        
        Scanner s = new Scanner(System.in);
        String response = "0";
        while (!response.equals("e")) {
            System.out.println("\nWelcome to the database; please input a number that corresponds to an entity related action:");
            System.out.println("1) Add new entity");
            System.out.println("2) Edit/Delete Entity");
            System.out.println("3) Search Entry by ID");

            System.out.println("Alternatively, input a letter that corresponds to an action:");
            System.out.println("a) Rent robot");
            System.out.println("b) Return equipment");
            System.out.println("c) Delivery of robots");
            System.out.println("d) Pickup robots");
            System.out.println("e) Exit program");

            System.out.println("Finally, these options relate to some helpful reports:");
            System.out.println("f) Report rental checkouts");
            System.out.println("g) Report popular robot");
            System.out.println("h) Report popular manufacturer");
            System.out.println("i) Report popular car");
            System.out.println("j) Report robots checked out");
            System.out.println("k) Report robots by type");

             response = s.nextLine().trim().toLowerCase();
            EntityManager.intializeAttributeLists();
            
            switch (response) {
                case "1":
                    EntityManager.addEntity(s, conn);
                    break;
                case "2":
                    EntityManager.editDeleteEntity(s, conn);
                    break;

                case "3":
                    EntityManager.searchEntity(s, conn);
                    break;

                case "a":
                    ActionManager.rentRobotPrompt(conn,s);
                    break;

                case "b":
                    ActionManager.returnEquipmentPrompt(conn,s);
                    break;

                case "c":
                    ActionManager.deliveryPrompt(conn,s);
                    break;

                case "d":
                    ActionManager.pickupPrompt(conn,s);
                    break;

                case "e":
                    System.out.println("Exiting program.");
                    break;



                case "f":
                    RentalMenu.reportRentingCheckouts(conn, s);
                    break;

                case "g":
                    RentalMenu.reportPopularRobot(conn);
                    break;

                case "h":
                    RentalMenu.reportPopularManufacturer(conn);
                    break;

                case "i":
                    RentalMenu.reportPopularCar(conn);
                    break;

                case "j":
                    RentalMenu.reportRobotsCheckedOut(conn);
                    break;
                
                case "k":
                    RentalMenu.reportRobotsByType(conn, s);
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        s.close();
    }
}