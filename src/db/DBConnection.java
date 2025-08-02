package db;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private final String url = "jdbc:postgresql://localhost:5433/mensworld_db";
    private final String user = "postgres";
    private final String password = "mensworld25";

    public Connection mkDataBase() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed, "+ e.getMessage());
//            e.printStackTrace();
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
           // System.exit(0);
        }
        return c;
    }
}
