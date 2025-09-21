package Models;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateTimeUtil
{
    // =====================
    // DATUM / DATE
    // =====================

    /**
     * Gibt das aktuelle Datum zurück.
     * Returns the current date.
     *
     * @return String im Format "dd.MM.yyyy", z.B. "21.09.2025"
     */
    public static String getCurrentDate()
    {
        // Aktuelles Datum ermitteln / Get current date
        LocalDate date = LocalDate.now();

        // Formatierung für Datum / Formatter for date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Rückgabe des formatierten Datums / Return formatted date
        return date.format(formatter);
    }

    // =====================
    // ZEIT / TIME
    // =====================

    /**
     * Gibt die aktuelle Uhrzeit zurück.
     * Returns the current time.
     *
     * @return String im Format "HH:mm:ss", z.B. "14:30:05"
     */
    public static String getCurrentTime()
    {
        // Aktuelle Uhrzeit ermitteln / Get current time
        LocalTime time = LocalTime.now();

        // Formatierung für Zeit / Formatter for time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Rückgabe der formatierten Zeit / Return formatted time
        return time.format(formatter);
    }

    // =====================
    // FEIERTAGE / HOLIDAYS
    // =====================

    /**
     * Gibt den nächsten Feiertag zurück.
     * Returns the next public holiday in Germany (BY – Bavaria).
     *
     * @return String im Format "dd.MM.yyyy Feiertagsname", z.B. "03.10.2025 Tag der Deutschen Einheit"
     */
    public static String getHoliday()
    {
        // Aktuelles Jahr / Current year
        LocalDate year = LocalDate.now();

        // Bundesland (BY = Bayern) / State (Bavaria)
        String united = "BY";
        String formattedDate = "";
        String holidayName = "";

        // HolidayManager von Jollyday instanziieren / Get HolidayManager instance
        HolidayManager manager = HolidayManager.getInstance(HolidayCalendar.GERMANY);

        // Feiertage des aktuellen Jahres abrufen / Get holidays for current year
        Set<Holiday> holidays = manager.getHolidays(year.getYear(), united);

        // Wenn Jahresende naht, auch nächstes Jahr prüfen / Also check next year if end of year
        holidays.addAll(manager.getHolidays(year.getYear() + 1 , united ));

        // Nächsten Feiertag finden / Find the next holiday
        Optional<Holiday> nextHoliday = holidays.stream()
                .filter(h -> !h.getDate().isBefore(year)) // nur zukünftige Feiertage / only future holidays
                .min(Comparator.comparing(Holiday::getDate));

        if (nextHoliday.isPresent())
        {
            Holiday holiday = nextHoliday.get();

            // Datum formatieren: z.B. "03.10.2025" / Format date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            formattedDate = holiday.getDate().format(formatter);

            // Name des Feiertags holen / Get holiday description
            holidayName = holiday.getDescription();
        }

        // Rückgabe: Datum + Feiertagsname / Return: date + holiday name
        return formattedDate + " " + holidayName;
    }

    // =====================
    // TERMINZEITEN / APPOINTMENT TIMES
    // =====================

    /**
     * Erstellt eine Liste von 30-Minuten-Takt-Terminen zwischen 08:00 und 17:00 Uhr.
     * Generates a list of appointment times in 30-minute intervals between 08:00 and 17:00.
     *
     * @return List<String> z.B. ["08:00", "08:30", "09:00", ..., "17:00"]
     */
    public static List<String> appointmentTimePicker()
    {
        // Liste für Uhrzeiten / List for times
        List<String> times = new ArrayList<>();

        // Startzeit / Start time
        LocalTime time = LocalTime.of(8, 0);

        // Endzeit / End time
        LocalTime end = LocalTime.of(17, 0);

        // Schleife über alle halbstündigen Zeitpunkte / Loop through all 30-min intervals
        while (!time.isAfter(end)) {
            times.add(time.format(DateTimeFormatter.ofPattern("HH:mm"))); // Uhrzeit als String hinzufügen / Add time as string
            time = time.plusMinutes(30); // 30 Minuten erhöhen / Increment by 30 minutes
        }

        return times;
    }
}
