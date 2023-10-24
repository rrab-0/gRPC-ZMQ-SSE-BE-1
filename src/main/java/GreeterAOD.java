import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/*
 * Starts DB instance, DB CRUD methods + Model for Greeter
 */
public class GreeterAOD {
    String identifier;
    String message;
    Connection globalDbConn;

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
    public Connection startDB(Map<String, String> envVariables) throws SQLException {
        String localhost = envVariables.get("DB_HOST");
        String port = envVariables.get("DB_PORT");
        String dbName = envVariables.get("DB_NAME");
        String dbUser = envVariables.get("DB_USER");
        String dbPassword = envVariables.get("DB_PASSWORD");

        String url = String.format("jdbc:postgresql://" + localhost + ":" + port + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword);
        Connection dbConn = DriverManager.getConnection(url);
        this.globalDbConn = dbConn;
        return dbConn;
    }

    // CRUD to DB
    public void create(GreeterAOD greeter) throws SQLException {
        // PreparedStatement st = this.globalDbConn.prepareStatement("INSERT INTO dump (identifier, message) VALUES (?, ?)");
        // st.setString(1, greeter.getIdentifier());
        PreparedStatement st = this.globalDbConn.prepareStatement("INSERT INTO dump (message) VALUES (?)");
        st.setString(1, greeter.getMessage());

        st.executeUpdate();
        st.close();
    }
}
