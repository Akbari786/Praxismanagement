package Models;

public class Login {
    public Login(int id, String user, String password,  EnumModels.UserRole role, String doc_Id, String personal_Id, boolean isLoggedIn) {
        User = user;
        Password = password;
        Id = id;
        UserRole = role;
        Doc_Id = doc_Id;
        Personal_Id = personal_Id;
        IsLoggedIn = isLoggedIn;

    }

    public String getUser() {
        return User;
    }

    public String getDoc_Id() {
        return Doc_Id;
    }

    public String getPersonal_Id() {
        return Personal_Id;
    }
    public int getId() {
        return Id;
    }

    public EnumModels.UserRole getUserRole() {
        return UserRole;
    }


    public boolean isLoggedIn() {
        return IsLoggedIn;
    }



    private final int Id;
    private final String User;
    private final String Password;
    private final EnumModels.UserRole UserRole;
    private final String Doc_Id;
    private final String Personal_Id;
    private final boolean IsLoggedIn;
}
