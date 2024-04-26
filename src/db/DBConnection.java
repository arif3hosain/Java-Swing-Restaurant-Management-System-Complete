package db;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private final String url = "jdbc:postgresql://localhost:5432/rms_db";
    private final String user = "postgres";
    private final String password = "123456";

   // public Connection con;
    public Connection mkDataBase() {

        Connection c = null;
        try {
           // Class.forName("org.hibernate.dialect.PostgreSQLDialect");
            c = DriverManager
                    .getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return c;
    }
 
}
