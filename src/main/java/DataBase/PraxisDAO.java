package DataBase;

import Models.EnumModels;
import Models.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class PraxisDAO {

    private static final Logger logger = LoggerFactory.getLogger(DashboardDAO.class);

    public List<Map<String, Object>> getVacationData(int year, int month) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Erster Tag und letzter Tag des Monats
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        String sql = """
            SELECT Personal_Number, Name, From_Date, To_Date, Note
            FROM (
                SELECT p.Personal_Number, p.Name, v.From_Date, v.To_Date, v.Note
                FROM personal p
                INNER JOIN vacations v ON p.Personal_Number = v.Personal_id
                WHERE v.Status = 'approved'
                  AND v.From_Date <= ?
                  AND v.To_Date >= ?
                UNION ALL
                SELECT d.Personal_Number, d.Name, v.From_Date, v.To_Date, v.Note
                FROM doctor d
                INNER JOIN vacations v ON d.Personal_Number = v.Doctor_id
                WHERE v.Status = 'approved'
                  AND v.From_Date <= ?
                  AND v.To_Date >= ?
            ) AS combined
            ORDER BY Personal_Number
            """;

        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Setze Parameter: letzter Tag des Monats / erster Tag des Monats
            ps.setDate(1, java.sql.Date.valueOf(lastDay));
            ps.setDate(2, java.sql.Date.valueOf(firstDay));
            ps.setDate(3, java.sql.Date.valueOf(lastDay));
            ps.setDate(4, java.sql.Date.valueOf(firstDay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Personal_Number", rs.getString("Personal_Number"));
                    map.put("Personal", rs.getString("Name"));
                    map.put("Start_Date", rs.getDate("From_Date") != null ? rs.getDate("From_Date").toLocalDate() : null);
                    map.put("End_Date", rs.getDate("To_Date") != null ? rs.getDate("To_Date").toLocalDate() : null);
                    map.put("VacationType", rs.getString("Note"));
                    result.add(map);
                }
            }

        } catch (SQLException e) {
            logger.error("Failed to get vacation requests: {}", e.getMessage(), e);
        }

        return result;
    }

    public boolean insertVacationForUser(Login user, EnumModels.VacationType type,
                                         LocalDate fromDate, LocalDate toDate, String note) {
        String sql = """
        INSERT INTO vacations (Personal_id, Doctor_id, From_Date, To_Date, Note, Status)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (user.getUserRole().equals(EnumModels.UserRole.Doctor)) {
                ps.setNull(1, Types.VARCHAR); // Persona_id NULL
                ps.setString(2, user.getDoc_Id()); // Doctor_id setzen
            } else { // Personal
                ps.setString(1, user.getPersonal_Id()); // Persona_id setzen
                ps.setNull(2, Types.VARCHAR); // Doctor_id NULL
            }

            ps.setDate(3, java.sql.Date.valueOf(fromDate));
            ps.setDate(4, java.sql.Date.valueOf(toDate));

            // VacationType als Note speichern
            ps.setString(5, type.name() + (note != null && !note.isEmpty() ? ": " + note : ""));

            ps.setString(6, "in progress"); // Status Englisch

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            logger.error("Failed to fetch vacation requests: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<Map<String, Object>> getVacationRequestsForUser(Login user) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "";

        try (Connection con = DatabaseConnector.getConnection()) {
            if (user.getUserRole().equals(EnumModels.UserRole.Doctor)) {
                sql = "SELECT From_Date, To_Date, Status FROM vacations WHERE Doctor_id = ?";
            } else if (user.getUserRole().equals(EnumModels.UserRole.Personal)) {
                sql = "SELECT From_Date, To_Date, Status FROM vacations WHERE Personal_id = ?";
            }

            PreparedStatement stmt = con.prepareStatement(sql);
            if (user.getUserRole().equals(EnumModels.UserRole.Doctor)) {
                stmt.setString(1, user.getDoc_Id());
            } else {
                stmt.setString(1, user.getPersonal_Id());
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("From_Date", rs.getDate("From_Date").toLocalDate());
                row.put("To_Date", rs.getDate("To_Date").toLocalDate());
                row.put("Status", rs.getString("Status"));
                result.add(row);
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch vacation requests: {}", e.getMessage(), e);
        }
        return result;
    }


}
