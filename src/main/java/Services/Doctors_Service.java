package Services;

import DataBase.DoctorDAO;
import Models.Doctor;
import Models.Personal;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public class Doctors_Service {
    private DoctorDAO doctorDAO;

    public Doctors_Service()
    {
        this.doctorDAO = new DoctorDAO();
    }
    public List<Doctor> getAllDoctors()
    {
        return doctorDAO.getAllDoctors();
    }
    public Optional<Doctor> getCurrentDoctor()
    {
        return doctorDAO.getDoctorsInfoFromCurrentUser();
    }


}
