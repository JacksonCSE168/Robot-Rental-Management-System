import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SQLManager {

    public static void sqlQuery(PreparedStatement sql){
        try {
            ResultSet rs = sql.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // ================= HEADER =================
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("| %-25s ", rsmd.getColumnName(i));
            }
            System.out.println("|");

            // ================= SEPARATOR =================
            for (int i = 1; i <= columnCount; i++) {
                System.out.print("+----------------------");
            }
            System.out.println("+");

            // ================= DATA =================
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {

                    String value = rs.getString(i);

                    if (rs.wasNull()) {
                        value = "NULL";
                    } else {
                        
                        if (rsmd.getColumnName(i).equalsIgnoreCase("start_date") 
                                && value.matches("\\d+")) {
                            long timestamp = Long.parseLong(value);
                            value = new java.sql.Date(timestamp).toString();
                        }
                    }

                    System.out.printf("| %-25s ", value);
                }
                System.out.println("|");
            }

            rs.close();
            sql.close();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public static void sqlUpdate(PreparedStatement sql){
        try {
            sql.executeUpdate();
            sql.close();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }
}