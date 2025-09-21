package DataBase;

import Models.Doctor;
import Models.Login;
import Models.Personal;
import Models.Session;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorDAO
{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DoctorDAO.class);

    public Optional<Doctor> getDoctorsInfoFromCurrentUser() {
        Login user = Session.getCurrentUser();
        if (user == null) {
            return Optional.empty();
        }
        String sql = "SELECT d.*, l.Password " +
                "FROM doctor d " +
                "JOIN login l ON d.Personal_Number = l.Doc_Id " +
                "WHERE d.Personal_Number = ?";
        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, user.getDoc_Id());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    // Alle Spalten aus dem ResultSet auslesen
                    String name = result.getString("Name");
                    String family = result.getString("Family");
                    String email = result.getString("Email");
                    String password = result.getString("Password");
                    String address = result.getString("Address");
                    String city = result.getString("City");
                    String cityCode = result.getString("City_Code");
                    String phoneNumber = result.getString("Phone_Number");
                    String personalNumber = result.getString("Personal_Number");
                    String specialty = result.getString("specialty");
                    java.sql.Date workingSinceDate = result.getDate("Working_Since");

                    Doctor doctors = new Doctor(
                            name,
                            family,
                            password,
                            personalNumber,
                            specialty,
                            email,
                            city,
                            address,
                            cityCode,
                            phoneNumber,
                            workingSinceDate != null ? new java.util.Date(workingSinceDate.getTime()) : null
                    );
                    doctors.setSpecialty(specialty);

                    return Optional.of(doctors);
                }
            }

        } catch (SQLException e) {
            logger.error("User info fetch failed: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }
    // List for ComboBox create appointment
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();

        String sql = "SELECT d.*, l.Password " +
                "FROM doctor d " +
                "JOIN login l ON d.Personal_Number = l.Doc_Id";

        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Doctor doctor = new Doctor(
                        result.getString("Name"),
                        result.getString("Family"),
                        result.getString("Password"),
                        result.getString("Personal_Number"),
                        result.getString("specialty"),
                        result.getString("Email"),
                        result.getString("City"),
                        result.getString("Address"),
                        result.getString("City_Code"),
                        result.getString("Phone_Number"),
                        result.getDate("Working_Since") != null
                                ? new java.util.Date(result.getDate("Working_Since").getTime())
                                : null
                );
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            logger.error("User info fetch failed: {}", e.getMessage(), e);
        }

        return doctors;
    }

}
