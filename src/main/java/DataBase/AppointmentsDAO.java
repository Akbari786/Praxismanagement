package DataBase;

import Models.Appointments;
import Models.EnumModels;

import java.sql.*;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentsDAO {
    private static final Logger logger = Logger.getLogger(AppointmentsDAO.class.getName());

    public static List<Appointments> getAllAppointmentsForToday(EnumModels.Appointment_Status status, boolean exclude) {
        List<Appointments> all_Appointment = new ArrayList<>();

        String sql = """
                SELECT
                        a.id AS Id,
                        DATE_FORMAT(a.appointment_time, '%H:%i') AS Appointment_time,
                        CONCAT(p.name, ' ', p.family) AS Patient,
                        p.birthdate AS Birthdate,
                        CONCAT(d.name, ' ', d.family) AS Doctor,
                        a.complaint AS Treatment,
                        a.Status as Status
                FROM
                        appointment a
                JOIN
                        patient p ON a.patient_id = p.insurance_number
                JOIN
                        doctor d ON a.doctor_id = d.Personal_Number
                WHERE
                        a.appointment_date = CURDATE()
                        AND a.status""" + (exclude ? "<> ?" : "= ?") + ";";

        try (Connection con = DatabaseConnector.getConnection(); PreparedStatement ps = con.prepareStatement(sql)

        ) {
            ps.setString(1, status.name());

            ResultSet result = ps.executeQuery();
            while (result.next()) {
                all_Appointment.add(new Appointments(result.getInt("Id"), result.getString("Patient"),
                        result.getString("Birthdate"), result.getString("Doctor"),
                        result.getString("Treatment"), result.getString("Appointment_time"),
                        result.getString("Status")));

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error in getAllAppointments", e.getMessage());
        }

        return all_Appointment;
    }

    public static boolean deleteAppointment(int id) {
        if (id <= 0) {
            System.err.println("Invalid ID for deletion:: " + id);
            return false;
        }

        String sql = "DELETE FROM appointment WHERE id = ?";


        try (Connection con = DatabaseConnector.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int affected = preparedStatement.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            logger.severe("Database error while deleting appointment: " + e.getMessage());
            return false;
        }
    }

    public static boolean nextAppointment(int id, EnumModels.Appointment_Status newStatus) {
        if (id <= 0) {
            System.err.println("Invalid ID for deletion:: " + id);
            return false;
        }

        String sql = "update appointment set status = ? where id = ?;";
        try (Connection con = DatabaseConnector.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {

            preparedStatement.setString(1, newStatus.name());
            preparedStatement.setInt(2, id);
            int affected = preparedStatement.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            logger.severe("Database error while next appointment: " + e.getMessage());
            return false;
        }
    }

    public static boolean createAppointment(String insuranceNumber, String doctorId, LocalDate date, String time, String note) {
        String sql = """
                INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, complaint, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, insuranceNumber);
            stmt.setString(2, doctorId);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setTime(4, java.sql.Time.valueOf(time + ":00"));
            stmt.setString(5, note);
            stmt.setString(6, EnumModels.Appointment_Status.Open.name());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(AppointmentsDAO.class.getName()).log(Level.SEVERE, "Error when adding the appointment", e);
            return false;
        }
    }

    public static List<String> getBookedAppointments(LocalDate date) {
        List<String> bookedTimes = new ArrayList<>();
        List<EnumModels.Appointment_Status> filteredStatuses = Arrays.stream(EnumModels.Appointment_Status.values()).filter(s -> s != EnumModels.Appointment_Status.Cancel) // nur Cancel ausschließen
                .toList();

        String placeholders = String.join(",", Collections.nCopies(filteredStatuses.size(), "?"));

        String sql = "SELECT Appointment_time FROM appointment " + "WHERE Appointment_date = ? " + "AND status IN (" + placeholders + ")";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(date));
            for (int i = 0; i < filteredStatuses.size(); i++) {
                stmt.setString(i + 2, filteredStatuses.get(i).name());
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String timeWithSeconds = rs.getString("Appointment_time"); // z.B. "14:00:00"
                // Formatieren auf HH:mm, also nur Stunden + Minuten
                if (timeWithSeconds != null && timeWithSeconds.length() >= 5) {
                    bookedTimes.add(timeWithSeconds.substring(0, 5)); // "14:00"
                }
            }
        } catch (SQLException e) {
            logger.severe("Database error while booked appointment: " + e.getMessage());
        }

        return bookedTimes;
    }

    public static Map<String, Integer> getWeeklyAppointmentsCount() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Monday", 0);
        counts.put("Tuesday", 0);
        counts.put("Wednesday", 0);
        counts.put("Thursday", 0);
        counts.put("Friday", 0);
        counts.put("Saturday", 0);
        counts.put("Sunday", 0);

        String sql = """
                SELECT WEEKDAY(appointment_date) AS wd, COUNT(*) as cnt
                FROM appointment
                        WHERE YEARWEEK(appointment_date, 1) = YEARWEEK(CURDATE(), 1)
                        GROUP BY wd
                        ORDER BY wd
                """;
        try (Connection con = DatabaseConnector.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                int wd = resultSet.getInt("wd");
                int cnt = resultSet.getInt("cnt");
                String dayName;
                switch (wd) {
                    case 0 -> dayName = "Monday";
                    case 1 -> dayName = "Tuesday";
                    case 2 -> dayName = "Wednesday";
                    case 3 -> dayName = "Thursday";
                    case 4 -> dayName = "Friday";
                    case 5 -> dayName = "Saturday";
                    case 6 -> dayName = "Sunday";

                    default -> dayName = null;
                }
                if (dayName != null) {
                    counts.put(dayName, cnt);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error in getWeeklyAppointmentsCount method", e.getMessage());
        }
        return counts;

    }
    public static Map<String, Integer> getAppointmentCountByMonthCurrentYear() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
                    SELECT
                        MONTH(appointment_date) AS month_num,
                        COUNT(*) AS appointment_count
                    FROM appointment
                    WHERE YEAR(appointment_date) = ?
                    GROUP BY month_num
                    ORDER BY month_num
                """;

        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Year.now().getValue());

            try (ResultSet rs = ps.executeQuery()) {
                String[] monthNames = {
                        "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"
                };
                for (String month : monthNames) {
                    result.put(month, 0);
                }

                while (rs.next()) {
                    int monthNum = rs.getInt("month_num");
                    int count = rs.getInt("appointment_count");
                    String monthName = monthNames[monthNum - 1];
                    result.put(monthName, count);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error in getWeeklyAppointmentsCount method", e.getMessage());
        }

        return result;
    }

}
