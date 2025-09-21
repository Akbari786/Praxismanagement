package Services;

import DataBase.DashboardDAO;
import Models.Personal;

import java.util.Optional;

public class Dashboard_Service
{
    public Optional<Personal> getCurrentPersonal()
    {
        return DashboardDAO.getPersonalInfoForCurrentUser();
    }
}
