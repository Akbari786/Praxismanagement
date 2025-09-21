package DataBase;

import Models.EnumModels;
import Models.Login;
import Models.Personal;
import Models.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DashboardDAO
{
    private static final Logger logger = LoggerFactory.getLogger(DashboardDAO.class);

    public static Optional<Personal> getPersonalInfoForCurrentUser()
    {

        Login user = Session.getCurrentUser();

        if (user == null)
        {
            return Optional.empty();
        }
        try (Connection con = DatabaseConnector.getConnection()) {
            String sql;
            PreparedStatement statement;

            if (user.getUserRole().equals(EnumModels.UserRole.Personal)) {
                sql = "SELECT p.Personal_Number, p.Name, p.Family FROM personal p WHERE p.Personal_Number = ?";
                statement = con.prepareStatement(sql);
                statement.setString(1, user.getPersonal_Id());

            } else if (user.getUserRole().equals(EnumModels.UserRole.Doctor)) {
                sql = "SELECT d.Personal_Number, d.Name, d.Family FROM doctor d WHERE d.Personal_Number = ?" ;

                statement = con.prepareStatement(sql);
                statement.setString(1, user.getDoc_Id());

            } else {
                return Optional.empty();
            }

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String personalNumber = result.getString("Personal_Number");
                String personalName = result.getString("Name");
                String personalFamily = result.getString("Family");

                Personal personal = new Personal(personalNumber, personalName, personalFamily);
                personal.setPersonal_Number(personalNumber);
                personal.setName(personalName);
                personal.setFamily(personalFamily);

                return Optional.of(personal);
            }

        } catch (SQLException e) {
            logger.error("User info fetch failed: {}", e.getMessage(), e);
        }

        return Optional.empty();


    }
}
