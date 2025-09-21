package Models;

public class Appointments {

    private final int id;
    private final String patient;
    private final String birthdate;
    private final String doctor;
    private final String treatment;
    private final String appointmentTime;
    private final String status;

    public Appointments(int id, String patient, String birthdate, String doctor, String treatment, String appointmentTime, String status) {
        this.id = id;
        this.patient = patient;
        this.birthdate = birthdate;
        this.doctor = doctor;
        this.treatment = treatment;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public int getId()
    {
        return id;
    }
    public String getPatient() {
        return patient;
    }
    public String getBirthdate() {
        return birthdate;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getTreatment() {
        return treatment;
    }
    public String getStatus() {
        return status;
    }



}
