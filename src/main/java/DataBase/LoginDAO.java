package DataBase;

import Models.EnumModels;
import Models.Login;
import java.sql.*;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginDAO {

    private static final Logger logger = LoggerFactory.getLogger(LoginDAO.class);

    public static Optional<Login> checkLogin(String username, String password)
    {
        String sql = "SELECT Id, User, Password, Role, Doc_Id, Personal_Id, Is_logged_in FROM login WHERE User = ? AND Password = ?";

        try (
                Connection con = DatabaseConnector.getConnection();
                PreparedStatement statement = con.prepareStatement(sql)
        ) {
            statement.setString(1, username.trim());
            statement.setString(2, password.trim());

            try (ResultSet result = statement.executeQuery())
            {
                if (result.next()) {
                    int id = result.getInt("Id");
                    String user = result.getString("User");
                    String pass = result.getString("Password");
                    String roleStr = result.getString("Role");
                    EnumModels.UserRole role = EnumModels.UserRole.valueOf(roleStr);
                    String docId = result.getString("Doc_Id");
                    String personalId = result.getString("Personal_Id");
                    boolean isLoggedIn = result.getBoolean("Is_logged_in");

                    if (!isLoggedIn)
                    {
                        setLoginState(user, pass);
                        Login login = new Login(id, user, pass, role, docId, personalId, isLoggedIn);
                        return Optional.of(login);
                    }
                    else
                    {
                        Login login = new Login(id, user, pass, role, docId, personalId, isLoggedIn);
                        return Optional.of(login);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error during login check: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }
    public static void setLoginState(String username, String password)
    {
        String sql = "UPDATE login SET Is_logged_in = ? WHERE User = ? AND Password = ? ";
        try (   Connection con = DatabaseConnector.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setBoolean(1, true);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("request failed: {}", e.getMessage(), e);

        }
    }
    public static void setLogOutState(int id)
    {
        String sql = "UPDATE login SET Is_logged_in = ? WHERE Id =  ? ";
        try (   Connection con = DatabaseConnector.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setBoolean(1, false);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("request failed: {}", e.getMessage(), e);
        }
    }



}
