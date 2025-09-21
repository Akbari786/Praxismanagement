package DataBase;

import Models.Patients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class PatientDAO
{
    private static final Logger logger = LoggerFactory.getLogger(PatientDAO.class);

    public static Patients getPatientById(String insuranceNumber)
    {
        String sql = "SELECT * FROM patient WHERE Insurance_number = ?";
        try(Connection con = DatabaseConnector.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql))
        {
            preparedStatement.setString(1, insuranceNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                return new Patients(
                        resultSet.getString("Insurance_number"),
                        resultSet.getString("Name"),
                        resultSet.getString("Family"),
                        resultSet.getString("Birthdate"),
                        resultSet.getString("Address"),
                        resultSet.getString("Phone_number"),
                        resultSet.getString("Email"),
                        resultSet.getString("Insurance")
                );
            }
        }
        catch (SQLException e)
        {
            logger.error("Failed to get patient requests: {}", e.getMessage(), e);
        }
        return null;
    }
}
