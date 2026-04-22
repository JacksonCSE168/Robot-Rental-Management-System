import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.sql.*;

public class EntityManager {

    static final LinkedHashMap<String, String> customerAttributeList = new LinkedHashMap<>();
    static final LinkedHashMap<String, String> robotAttributeList = new LinkedHashMap<>();

    static void intializeAttributeLists() {
        
        customerAttributeList.clear();
        robotAttributeList.clear();
        customerAttributeList.put("customer_id", "integer");
        customerAttributeList.put("first_name", "string");
        customerAttributeList.put("last_name", "string");
       
        customerAttributeList.put("city", "string");
        customerAttributeList.put("state", "string");
        customerAttributeList.put("zip_code", "string");
        customerAttributeList.put("phone", "string");
        customerAttributeList.put("email", "string");
        customerAttributeList.put("start_date", "date");
        customerAttributeList.put("status", "string");
        customerAttributeList.put("facility_distance", "double");
        customerAttributeList.put("facility_id", "integer");

        robotAttributeList.put("robot_id", "integer");
        robotAttributeList.put("name", "string");
        robotAttributeList.put("serial_number", "string");
        robotAttributeList.put("status", "string");
        robotAttributeList.put("warehouse_location", "string");
        robotAttributeList.put("model", "string");
        robotAttributeList.put("order_request_no", "integer");
        robotAttributeList.put("year", "integer");
        robotAttributeList.put("training_level", "integer");
        robotAttributeList.put("warranty_expiration", "date");
        robotAttributeList.put("facility_id", "integer");
    }

    static final Set<String> entities = Set.of("Customer", "Robot");

    public static String nextSanitizedLine(Scanner s) {
        String line = s.nextLine();
        return line.replaceAll("[^a-zA-Z0-9@._:\\-,]", "");
    }

    public static String promptEntities(Scanner s) {
        for (String entity : entities) {
            System.out.println(entity);
        }
        return nextSanitizedLine(s);
    }

    private static boolean isInteger(String s){
        boolean result = true;
        try {
            Integer.parseInt(s);
        }catch(NumberFormatException e){
            result = false;
        }
        return result;
    }

    private static boolean isDouble(String s){
        boolean result = true;
        try {
            Double.parseDouble(s);
        }catch(NumberFormatException e){
            result = false;
        }
        return result;
    }

    private static boolean isDate(String s){
        boolean result = true;
        try {
            Date.valueOf(s);
        } catch (IllegalArgumentException e){
            result = false;
        }
        return result;
    }

    private static String buildInsertSQL(String table, LinkedHashMap<String, String> map) {
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();

        for (String key : map.keySet()) {
            cols.append(key).append(", ");
            vals.append("?, ");
        }

        cols.setLength(cols.length() - 2);
        vals.setLength(vals.length() - 2);

        return "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";
    }

    public static void addEntity(Scanner s, Connection conn){
         System.out.println("Which type of entity would you like to add?");
        String chosenEntity = promptEntities(s);

        if (!entities.contains(chosenEntity)) {
            System.out.println("Invalid entity.");
            return;
        }

        LinkedHashMap<String, String> map =
                chosenEntity.equals("Customer") ? customerAttributeList : robotAttributeList;

        String sql = buildInsertSQL(chosenEntity, map);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;

            for (String attr : map.keySet()) {
                String type = map.get(attr);

                switch (type) {
                    case "string":
                        System.out.println(attr + ":");
                        stmt.setString(index, nextSanitizedLine(s));
                        break;

                    case "integer":
                        String intVal;
                        do {
                            System.out.println(attr + " (int):");
                            intVal = nextSanitizedLine(s);
                        } while (!isInteger(intVal));
                        stmt.setInt(index, Integer.parseInt(intVal));
                        break;

                    case "double":
                        String dblVal;
                        do {
                            System.out.println(attr + " (double):");
                            dblVal = nextSanitizedLine(s);
                        } while (!isDouble(dblVal));
                        stmt.setDouble(index, Double.parseDouble(dblVal));
                        break;

                    case "date":
                        String dateVal;
                        do {
                            System.out.println(attr + " (YYYY-MM-DD):");
                            dateVal = nextSanitizedLine(s);
                        } while (!isDate(dateVal));
                       stmt.setString(index, dateVal);
                        break;
                }

                index++;
            }

            stmt.executeUpdate();
            System.out.println("Inserted successfully.");

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }
    

    public static void editDeleteEntity (Scanner s, Connection conn){
    System.out.println("Which type of entity would you like to edit/delete?");
    String chosenEntity = promptEntities(s);
    String sql = null;
    PreparedStatement stmt = null;

    if (!entities.contains(chosenEntity)) {
        System.out.println("Invalid entity name, please try again.");
        return;
    }

    System.out.println("Please enter the ID of the entry you want to edit/delete:");
    int editId = Integer.parseInt(nextSanitizedLine(s));

    System.out.println("Would you like to edit or delete this entry? (Type 'edit' or 'delete')");
    String editOrDelete = nextSanitizedLine(s);

    // ================= DELETE =================
    if (editOrDelete.equalsIgnoreCase("delete")) {
        sql = "DELETE FROM " + chosenEntity + " WHERE " + chosenEntity.toLowerCase() + "_id = ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, editId);
            SQLManager.sqlUpdate(stmt);
            System.out.println("Record deleted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= EDIT =================
    else if (editOrDelete.equalsIgnoreCase("edit")) {

        LinkedHashMap<String, String> editMap =
                chosenEntity.equals("Customer") ? customerAttributeList : robotAttributeList;

        System.out.println("Which attribute would you like to edit?");
        for (String attribute : editMap.keySet()) {
            System.out.println(attribute);
        }

        String attributeToEdit = nextSanitizedLine(s);

        
        String actualKey = null;
        for (String key : editMap.keySet()) {
            if (key.equalsIgnoreCase(attributeToEdit)) {
                actualKey = key;
                break;
            }
        }

        if (actualKey == null) {
            System.out.println("Invalid attribute name.");
            return;
        }

        attributeToEdit = actualKey;

        
        sql = "UPDATE " + chosenEntity + " SET " + attributeToEdit + " = ? WHERE "
                + chosenEntity.toLowerCase() + "_id = ?";

        try {
            stmt = conn.prepareStatement(sql);
            String attributeType = editMap.get(attributeToEdit);

            System.out.println("Enter new value:");

            switch (attributeType) {
                case "string":
                    stmt.setString(1, nextSanitizedLine(s));
                    break;

                case "integer":
                    String intInput = "";
                    while(!isInteger(intInput)){
                        intInput = nextSanitizedLine(s);
                    }
                    stmt.setInt(1, Integer.parseInt(intInput));
                    break;

                case "double":
                    String doubleInput = "";
                    while(!isDouble(doubleInput)){
                        doubleInput = nextSanitizedLine(s);
                    }
                    stmt.setDouble(1, Double.parseDouble(doubleInput));
                    break;

                case "date":
                    String dateInput = "";
                    while(!isDate(dateInput)){
                        dateInput = nextSanitizedLine(s);
                    }
                    stmt.setString(1, dateInput); 
                    break;
            }

            stmt.setInt(2, editId);
            SQLManager.sqlUpdate(stmt);
            System.out.println("Record updated successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    else {
        System.out.println("Invalid option, please try again.");
    }
}
    public static void searchEntity(Scanner s, Connection conn){
        System.out.println("Which type of entity would you like to search for?");
        String chosenEntity = promptEntities(s);
        String sql = null;
        PreparedStatement stmt = null;

        if (!entities.contains(chosenEntity)) {
            System.out.println("Invalid entity name, please try again.");
        } else {

            System.out.println("Please enter the ID of the entry you want to search for:");
            int id = Integer.parseInt(nextSanitizedLine(s));

            sql = "SELECT * FROM " + chosenEntity + " WHERE " + chosenEntity.toLowerCase() + "_id = ?";

            try {
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                SQLManager.sqlQuery(stmt);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
