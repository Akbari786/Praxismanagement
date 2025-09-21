package DataBase;
import Models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PersonalDAO
{
    private static final Logger logger = LoggerFactory.getLogger(PersonalDAO.class);

    public static Optional<Personal> getPersonalInfoFromCurrentUser()
    {
        Login user = Session.getCurrentUser();
        if (user == null) {
            return Optional.empty();
        }
        String sql = "SELECT p.*, l.Password " +
                "FROM personal p " +
                "JOIN login l ON p.Personal_Number = l.Personal_Id " +
                "WHERE p.Personal_Number = ?";
        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, user.getPersonal_Id());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    // Alle Spalten aus dem ResultSet auslesen
                    String name = result.getString("Name");
                    String family = result.getString("Family");
                    String gender = result.getString("Gender");
                    String email = result.getString("Email");
                    String password = result.getString("Password");
                    String address = result.getString("Address");
                    String city = result.getString("City");
                    String cityCode = result.getString("City_Code");
                    String phoneNumber = result.getString("Phone_Number");
                    String personalNumber = result.getString("Personal_Number");
                    String specialty = result.getString("specialty");
                    java.sql.Date workingSinceDate = result.getDate("Working_Since");

                    Personal personal = new Personal(
                            name,
                            family,
                            email,
                            password,
                            address,
                            city,
                            cityCode,
                            personalNumber,
                            phoneNumber,
                            workingSinceDate != null ? new java.util.Date(workingSinceDate.getTime()) : null
                    );
                    personal.setSpecialty(specialty);

                    return Optional.of(personal);
                }
            }

        } catch (SQLException e) {
            logger.error("User info fetch failed: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }

}
