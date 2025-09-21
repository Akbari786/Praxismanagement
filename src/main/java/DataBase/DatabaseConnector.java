package DataBase;
import java.sql.*;

public class DatabaseConnector {
    private static final String URL = ""; // Datenbank-URL anpassen
    private static final String USER = ""; // Benutzername anpassen
    private static final String PASSWORD = ""; // Passwort anpassen

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}


