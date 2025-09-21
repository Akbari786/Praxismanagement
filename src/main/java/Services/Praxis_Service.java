package Services;

import DataBase.PraxisDAO;
import Models.EnumModels;
import Models.Login;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class Praxis_Service {

    private final PraxisDAO dao = new PraxisDAO();

    /**
     * Liefert alle Urlaubsdaten für einen Monat
     */
    public List<Map<String, Object>> getVacationData(int year, int month) {
        return dao.getVacationData(year, month);
    }

    /**
     * Liefert die Anzahl der Tage in einem Monat
     */
    public int getDaysInMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return ym.lengthOfMonth();
    }

    public boolean requestVacationForCurrentUser(EnumModels.VacationType type,
                                                 LocalDate fromDate, LocalDate toDate, String note) {
        Login currentUser = Models.Session.getCurrentUser();
        if (currentUser == null) return false;

        return new PraxisDAO().insertVacationForUser(currentUser, type, fromDate, toDate, note);
    }
    public List<Map<String, Object>> getVacationRequestsForCurrentUser() {
        Login currentUser = Models.Session.getCurrentUser();
        if (currentUser == null) return List.of();
        return dao.getVacationRequestsForUser(currentUser);
    }


}
