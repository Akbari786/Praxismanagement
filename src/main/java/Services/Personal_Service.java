package Services;

import DataBase.PersonalDAO;
import Models.Personal;

import java.util.Optional;

public class Personal_Service
{
    public Optional<Personal> getCurrentPersonal()
    {
        return PersonalDAO.getPersonalInfoFromCurrentUser();
    }


}
