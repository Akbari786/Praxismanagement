package Models;

public class
Session
{
    private static Login currentUser;

    public static void setCurrentUser(Login user)
    {
        currentUser = user;
    }
    public static Login getCurrentUser()
    {
        return  currentUser;
    }
    public static void clear()
    {
        currentUser = null;
    }
}
