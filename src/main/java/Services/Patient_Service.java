package Services;

import DataBase.PatientDAO;
import Models.Patients;

public class Patient_Service
{
    public Patients getPatientById(String insuranceNumber) {
        return PatientDAO.getPatientById(insuranceNumber);
    }

}
