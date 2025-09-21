package Services;

import DataBase.AppointmentsDAO;
import Models.Appointments;
import Models.EnumModels;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Appointments_Service
{
    public List<Appointments> getAppointmentsForToday(EnumModels.Appointment_Status status, boolean exclude)
    {
       return AppointmentsDAO.getAllAppointmentsForToday(status, exclude);
    }
    public List<String> getBookedAppointments(LocalDate date) {
        return AppointmentsDAO.getBookedAppointments(date);
    }

    public boolean createAppointment(String insuranceNumber, String doctorNumber, LocalDate date, String time, String note) {
        return AppointmentsDAO.createAppointment(insuranceNumber, doctorNumber, date, time, note);
    }

    public boolean deleteAppointment(int appointmentId) {
        return AppointmentsDAO.deleteAppointment(appointmentId);
    }

    public boolean nextAppointment(int appointmentId, EnumModels.Appointment_Status status) {
        return AppointmentsDAO.nextAppointment(appointmentId, status);
    }

    public java.util.Map<String, Integer> getWeeklyAppointmentsCount() {
        return AppointmentsDAO.getWeeklyAppointmentsCount();
    }
    public  Map<String, Integer> getAppointmentCountByMonthCurrentYear()
    {
        return AppointmentsDAO.getAppointmentCountByMonthCurrentYear();
    }

}
