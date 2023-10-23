import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * Starts DB instance, DB CRUD methods + Model for Greeter
 */
public class GreeterAOD {
    String identifier;
    String message;

    // Getters and setters
    public String getIdentifier() {
        return identifier;
    }

    public String getMessage() {
        return message;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Start DB
    public static Connection startDB() {
        try {
            String url = "jdbc:postgresql://localhost/grpc-zmq-sse-be-1?user=postgres&password=123";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // CRUD to DB
    public static void create(Connection dbConn, GreeterAOD greeter) {
        try {
            PreparedStatement st = dbConn.prepareStatement("INSERT INTO dump (identifier, message) VALUES (?, ?)");
            st.setString(1, greeter.getIdentifier());
            st.setString(2, greeter.getMessage());
            st.executeUpdate();

            st.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
