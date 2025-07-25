package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private final String url = "jdbc:postgresql://localhost:5433/rms";
    private final String user = "postgres";
    private final String password = "postgres";

    public Connection mkDataBase() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver"); // ✅ FIXED
            c = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
    }
}
