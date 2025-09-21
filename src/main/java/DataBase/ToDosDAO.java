package DataBase;


import Models.*;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToDosDAO
{


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ToDosDAO.class);

    public static List<ToDos> getToDosList()
    {
        List<ToDos> toDosList = new ArrayList<>();
        Login user = Session.getCurrentUser();

        if (user == null)
        {
            return toDosList;
        }

       EnumModels.UserRole userRole = user.getUserRole();

        try (Connection con = DatabaseConnector.getConnection())
        {
            String sql;
            PreparedStatement statement;
            if (userRole.equals(EnumModels.UserRole.Doctor))
            {
                String doc_id = user.getDoc_Id();
                sql = "SELECT Id, Title, Description, Personal_Id, Doctor_Id, Due_date, Is_done, Priority, Created_at " +
                        "from todos " +
                        "where Doctor_Id = ? ";
                statement = con.prepareStatement(sql);
                statement.setString(1, doc_id );
            } else if (userRole.equals(EnumModels.UserRole.Personal))
            {
                String personal_id = user.getPersonal_Id();
                sql = "SELECT Id, Title, Description, Personal_Id, Doctor_Id, Due_date, Is_done, Priority, Created_at " +
                        "from todos " +
                        "where Personal_Id  = ? ";
                statement = con.prepareStatement(sql);
                statement.setString(1, personal_id);
            }
            else
            {
                throw new IllegalStateException("Unbekannte Rolle: " + userRole);
            }
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ToDos toDos = new ToDos(
                        result.getInt("Id"),
                        result.getString("Title"),
                        result.getString("Description"),
                        result.getString("Personal_Id"),
                        result.getString("Doctor_Id"),
                        result.getDate("Due_date"),
                        result.getBoolean("Is_done"),
                        result.getString("Priority"),
                        result.getTimestamp("Created_at")
                );
                toDosList.add(toDos);
            }

        }
        catch (SQLException e)
        {
            logger.error("Error to connect to the Database:", e);
        }

        return toDosList;
    }

}
